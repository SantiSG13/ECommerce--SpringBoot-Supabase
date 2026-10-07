package com.example.demo.Controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Modelos.DAO.InterfaceClienteDAO;
import com.example.demo.Modelos.DAO.InterfaceUsuarioDAO;
import com.example.demo.Modelos.Entity.Cliente;
import com.example.demo.Modelos.Entity.Usuario;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private InterfaceUsuarioDAO usuarioDao;

    @Autowired
    private InterfaceClienteDAO clienteDao;

    // Dashboard principal del admin
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("titulo", "Panel de Administración");
        model.addAttribute("totalClientes", usuarioDao.findByHabilitado(true).stream()
                .filter(u -> "ROLE_CLIENTE".equals(u.getRol())).count());
        model.addAttribute("pendientes", usuarioDao.findByHabilitado(false).size());
        return "dashboard";
    }

    // Panel principal: pendientes + activos
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("titulo", "Gestión de Usuarios");
        model.addAttribute("pendientes", usuarioDao.findByHabilitado(false));
        model.addAttribute("activos", usuarioDao.findByHabilitado(true));
        return "gestionUsuarios";
    }

    // Aprobar usuario y asignarle un rol
    @PostMapping("/aprobar/{id}")
    public String aprobar(@PathVariable Long id,
                          @RequestParam("rol") String rol,
                          RedirectAttributes redirectAttrs) {

        Usuario usuario = usuarioDao.findById(id);
        if (usuario != null) {
            usuario.setRol(rol);
            usuario.setHabilitado(true); //Aqui habilitamos a los usuarios nuevos
            usuarioDao.save(usuario);

            // Si se aprueba como ROLE_CLIENTE, crear registro en la tabla clientes
            if ("ROLE_CLIENTE".equals(rol)) {
                Cliente cliente = new Cliente();

                // Nombre
                if (usuario.getNombre() != null) {
                    cliente.setNombre(usuario.getNombre());
                } else {
                    cliente.setNombre(usuario.getUsuario());
                }

                // Apellido
                if (usuario.getApellido() != null) {
                    cliente.setApellido(usuario.getApellido());
                } else {
                    cliente.setApellido("");
                }

                // Email
                if (usuario.getEmail() != null) {
                    cliente.setEmail(usuario.getEmail());
                } else {
                    cliente.setEmail("");
                }

                cliente.setCreateAt(new Date());
                clienteDao.save(cliente);

                // Vincular el cliente recién creado al usuario
                usuario.setCliente(cliente);
                usuarioDao.save(usuario);
            }

            redirectAttrs.addFlashAttribute("success",
                "Usuario '" + usuario.getUsuario() + "' aprobado como " + rol + ".");
        }
        return "redirect:/admin/usuarios";
    }

    // Cambiar rol de un usuario ya activo
    @PostMapping("/cambiarRol/{id}")
    public String cambiarRol(@PathVariable Long id,
                             @RequestParam("rol") String rol,
                             RedirectAttributes redirectAttrs) {

        Usuario usuario = usuarioDao.findById(id);
        if (usuario != null) {
            usuario.setRol(rol);
            usuarioDao.save(usuario);
            redirectAttrs.addFlashAttribute("success",
                "Rol de '" + usuario.getUsuario() + "' actualizado a " + rol + ".");
        }
        return "redirect:/admin/usuarios";
    }

    // Deshabilitar un usuario activo
    @PostMapping("/deshabilitar/{id}")
    public String deshabilitar(@PathVariable Long id,
                               RedirectAttributes redirectAttrs) {

        Usuario usuario = usuarioDao.findById(id);
        if (usuario != null) {
            usuario.setHabilitado(false);
            usuarioDao.save(usuario);
            redirectAttrs.addFlashAttribute("success",
                "Usuario '" + usuario.getUsuario() + "' deshabilitado.");
        }
        return "redirect:/admin/usuarios";
    }
}
