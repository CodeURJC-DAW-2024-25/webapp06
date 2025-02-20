package es.codeurjc.global_mart.controller;

import es.codeurjc.global_mart.service.UserService;
import es.codeurjc.global_mart.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Asegurarse de importar Controller
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginRegisterController {

    @Autowired
    private UserService userService;

    // Procesa el registro del usuario
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
            @RequestParam String mail,
            @RequestParam String password,
            @RequestParam boolean isCompany) {
        // Imprime en consola para comprobar los datos
        System.out.println(
                "Registro recibido - username: " + username + ", email: " + mail + ", isCompany: " + isCompany);
        userService.createUser(username, mail, password, isCompany);
        return "redirect:/login"; // Redirecciona a la página de login tras el registro
    }
}