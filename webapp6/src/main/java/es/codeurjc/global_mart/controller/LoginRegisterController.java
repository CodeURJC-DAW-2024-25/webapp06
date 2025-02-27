package es.codeurjc.global_mart.controller;

import es.codeurjc.global_mart.service.UserService;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Asegurarse de importar Controller
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class LoginRegisterController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    // Procesa el registro del usuario
    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String username, // Recibe los datos del
                                                                                         // formulario
            @RequestParam String mail,
            @RequestParam String password,
            @RequestParam MultipartFile image,
            @RequestParam String role) throws Exception {
        System.out.println("Registering user: " + name + " " + username + " " + mail + " " + password + " " + role); // Imprime
                                                                                                                     // los
                                                                                                                     // datos
                                                                                                                     // del
                                                                                                                     // usuario
                                                                                                                     // en
                                                                                                                     // la
                                                                                                                     // consola
        userService.createUser(image, name, username, mail, passwordEncoder.encode(password), List.of(role)); // Llama
                                                                                                              // al
                                                                                                              // método
                                                                                                              // createUser
                                                                                                              // del
        // servicio

        return "redirect:/"; // Redirecciona a la página de login tras el registro
    }
}