package com.example.demo.Controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Modelos.DAO.InterfaceUsuarioDAO;
import com.example.demo.Modelos.Entity.Usuario;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private InterfaceUsuarioDAO usuarioDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("titulo", "Crear Cuenta");
        return "registro";
    }

    @PostMapping
    public String registrar(@RequestParam("usuario") String usuarioNombre,
                            @RequestParam("nombre") String nombre,
                            @RequestParam("apellido") String apellido,
                            @RequestParam("email") String email,
                            @RequestParam("password") String password,
                            @RequestParam("confirmar") String confirmar,
                            Model model) {

        model.addAttribute("titulo", "Crear Cuenta");

        // Validar que las contraseñas coincidan
        if (!password.equals(confirmar)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "registro";
        }

        // Validar que el nombre de usuario no exista ya
        if (usuarioDao.findByUsuario(usuarioNombre) != null) {
            model.addAttribute("error", "El nombre de usuario ya está en uso.");
            return "registro";
        }

        // Crear el usuario pendiente con sus datos personales
        Usuario nuevo = new Usuario();
        nuevo.setUsuario(usuarioNombre);
        nuevo.setContraseña(passwordEncoder.encode(password));
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setEmail(email);
        nuevo.setRol(null);
        nuevo.setHabilitado(false);
        nuevo.setCreateAt(new Date());

        usuarioDao.save(nuevo);

        model.addAttribute("success", "Cuenta creada. Espera a que un administrador apruebe tu acceso.");
        return "registro";
    }
}
