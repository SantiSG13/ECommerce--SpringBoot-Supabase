package com.example.demo.Controllers;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(@RequestParam(value="error", required=false) String error,
                        @RequestParam(value="logout", required=false) String logout,
                        Model model, Principal principal){
        
        if(principal != null){
            // Si el usuario ya esta autenticado, redirigir a la vista principal
            return "redirect:/Cliente/Listar";
        }
        if (error != null) {
            // Si hay un error, mostrar un mensaje de error
            model.addAttribute("error", "Error en el login");
        }
        if (logout != null) {
            // Si hay un logout, mostrar un mensaje de logout
            model.addAttribute("success", "Has cerrado sesión");
        }
        return "login";
    }
}