package com.example.demo.Controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Modelos.DAO.InterfaceClienteDAO;
import com.example.demo.Modelos.DAO.InterfaceCompraDAO;
import com.example.demo.Modelos.DAO.InterfaceProductoDAO;
import com.example.demo.Modelos.DAO.InterfaceUsuarioDAO;
import com.example.demo.Modelos.DTO.ItemCarrito;
import com.example.demo.Modelos.Entity.Cliente;
import com.example.demo.Modelos.Entity.Compra;
import com.example.demo.Modelos.Entity.DetalleCompra;
import com.example.demo.Modelos.Entity.Producto;
import com.example.demo.Modelos.Entity.Usuario;

import jakarta.servlet.http.HttpSession;

@Controller
public class CompraController {

    @Autowired
    private InterfaceProductoDAO productoDAO;

    @Autowired
    private InterfaceCompraDAO compraDAO;

    @Autowired
    private InterfaceUsuarioDAO usuarioDAO;

    @Autowired
    private InterfaceClienteDAO clienteDAO;

    // ==========================================
    // 1. GESTIÓN DEL CARRITO DE COMPRAS (SESIÓN)
    // ==========================================

    // Agregar un producto al carrito guardado en la sesión HTTP
    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam("productoId") Long productoId,
                                   @RequestParam(value = "cantidad", defaultValue = "1") Integer cantidad,
                                   HttpSession session,
                                   RedirectAttributes redirectAttrs) {

        Producto producto = productoDAO.findOne(productoId);
        if (producto == null) {
            redirectAttrs.addFlashAttribute("error", "El producto no existe.");
            return "redirect:/Producto/listar";
        }

        if (cantidad <= 0) {
            redirectAttrs.addFlashAttribute("error", "La cantidad debe ser mayor a cero.");
            return "redirect:/Producto/listar";
        }

        // Obtener el carrito de la sesión o crearlo si aún no existe
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute("carrito", carrito);
        }

        // Verificar si el producto ya está en el carrito para sumar la cantidad
        boolean yaExiste = false;
        for (ItemCarrito item : carrito) {
            if (item.getProducto().getId().equals(producto.getId())) {
                int nuevaCantidad = item.getCantidad() + cantidad;
                if (nuevaCantidad > producto.getStock()) {
                    redirectAttrs.addFlashAttribute("error", 
                        "No hay suficiente stock. Stock disponible: " + producto.getStock());
                    return "redirect:/Producto/listar";
                }
                item.setCantidad(nuevaCantidad);
                yaExiste = true;
                break;
            }
        }

        // Si es un producto nuevo en el carrito
        if (!yaExiste) {
            if (cantidad > producto.getStock()) {
                redirectAttrs.addFlashAttribute("error", 
                    "No hay suficiente stock. Stock disponible: " + producto.getStock());
                return "redirect:/Producto/listar";
            }
            carrito.add(new ItemCarrito(producto, cantidad));
        }

        redirectAttrs.addFlashAttribute("success", 
            "'" + producto.getNombre() + "' fue agregado al carrito.");
        return "redirect:/Producto/listar";
    }

    // Ver los productos que el cliente ha agregado a su carrito
    @GetMapping("/carrito/ver")
    public String verCarrito(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }

        // Calcular el total general del carrito
        double total = carrito.stream().mapToDouble(ItemCarrito::getSubtotal).sum();

        model.addAttribute("titulo", "Carrito de Compras");
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);

        return "carrito";
    }

    // Eliminar un producto del carrito
    @GetMapping("/carrito/eliminar/{productoId}")
    public String eliminarDelCarrito(@PathVariable Long productoId, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.removeIf(item -> item.getProducto().getId().equals(productoId));
        }
        return "redirect:/carrito/ver";
    }

    // Vaciar por completo el carrito
    @GetMapping("/carrito/vaciar")
    public String vaciarCarrito(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/carrito/ver";
    }

    // ==========================================
    // 2. PROCESAR LA COMPRA (CREAR FACTURA)
    // ==========================================

    @PostMapping("/carrito/comprar")
    public String finalizarCompra(HttpSession session,
                                  Principal principal,
                                  RedirectAttributes redirectAttrs) {

        @SuppressWarnings("unchecked")
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "El carrito está vacío.");
            return "redirect:/carrito/ver";
        }

        // 1. Obtener el cliente actual a través del usuario autenticado
        Usuario usuario = usuarioDAO.findByUsuario(principal.getName());
        Cliente cliente = (usuario != null) ? usuario.getCliente() : null;

        // Si el usuario no tiene cliente asociado directamente, creamos uno de respaldo
        if (cliente == null) {
            cliente = new Cliente();
            cliente.setNombre(usuario != null ? usuario.getNombre() : "Cliente");
            cliente.setApellido(usuario != null ? usuario.getApellido() : "");
            cliente.setEmail(usuario != null ? usuario.getEmail() : "");
            cliente.setCreateAt(new Date());
            clienteDAO.save(cliente);

            if (usuario != null) {
                usuario.setCliente(cliente);
                usuarioDAO.save(usuario);
            }
        }

        // 2. Crear el objeto Encabezado de Compra
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setFecha(new Date());

        // 3. Crear los Detalles y actualizar el stock de cada producto
        for (ItemCarrito item : carrito) {
            Producto prodActual = productoDAO.findOne(item.getProducto().getId());

            // Validar stock antes de persistir
            if (prodActual.getStock() < item.getCantidad()) {
                redirectAttrs.addFlashAttribute("error", 
                    "Stock insuficiente para: " + prodActual.getNombre() + " (Disponibles: " + prodActual.getStock() + ")");
                return "redirect:/carrito/ver";
            }

            // Descontar inventario
            prodActual.setStock(prodActual.getStock() - item.getCantidad());
            productoDAO.save(prodActual);

            // Crear el detalle
            DetalleCompra detalle = new DetalleCompra();
            detalle.setProducto(prodActual);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(prodActual.getPrecio()); // Congelamos el precio actual

            // Asociar detalle a la compra
            compra.addDetalle(detalle);
        }

        // 4. Calcular el total y guardar la compra con sus detalles en cascada
        compra.calcularTotal();
        compraDAO.save(compra);

        // 5. Limpiar el carrito de la sesión
        session.removeAttribute("carrito");

        // 6. Redirigir a la vista de la factura recién generada
        return "redirect:/compras/factura/" + compra.getId();
    }

    // ==========================================
    // 3. MOSTRAR FACTURA / HISTORIAL
    // ==========================================

    @GetMapping("/compras/factura/{id}")
    public String verFactura(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        Compra compra = compraDAO.findOne(id);
        if (compra == null) {
            redirectAttrs.addFlashAttribute("error", "Factura no encontrada.");
            return "redirect:/Producto/listar";
        }

        model.addAttribute("titulo", "Factura de Venta #" + compra.getId());
        model.addAttribute("compra", compra);

        return "factura";
    }

    @GetMapping("/compras/mis-compras")
    public String misCompras(Principal principal, Model model) {
        Usuario usuario = usuarioDAO.findByUsuario(principal.getName());
        List<Compra> compras = new ArrayList<>();

        if (usuario != null && usuario.getCliente() != null) {
            compras = compraDAO.findByClienteId(usuario.getCliente().getId());
        } else if (usuario != null && "ROLE_ADMIN".equals(usuario.getRol())) {
            // El admin puede ver todas las compras
            compras = compraDAO.findAll();
        }

        model.addAttribute("titulo", "Historial de Compras");
        model.addAttribute("compras", compras);
        return "misCompras";
    }
}
