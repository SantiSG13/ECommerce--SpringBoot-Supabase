package com.example.demo.Controllers;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model,
                        Principal principal,
                        Authentication authentication,
                        jakarta.servlet.http.HttpSession session) {

        // Si ya está autenticado, redirigir según su rol
        if (principal != null && authentication != null) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/Producto/listar";
            }
        }

        if (error != null) {
            Object ex = session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            if (ex != null && ex.toString().contains("disabled")) {
                model.addAttribute("error", "Tu cuenta está pendiente de aprobación por un administrador.");
            } else {
                model.addAttribute("error", "Usuario o contraseña incorrectos.");
            }
        }

        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión");
        }

        return "login";
    }
}
