package es.codeurjc.global_mart.controller;

import es.codeurjc.global_mart.service.UserService;
import es.codeurjc.global_mart.model.User;

import java.util.List;
import java.util.Optional;

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
    public String registerUser(@RequestParam String username, // Recibe los datos del formulario
            @RequestParam String mail,
            @RequestParam String password,
            @RequestParam List<String> role) {
        // Imprime en consola para comprobar los datos
        System.out.println(
                "Registro recibido - username: " + username + ", email: " + mail + ", role: " + role);
        userService.createUser(username, mail, password, role); // Llama al método createUser del servicio
        return "redirect:/"; // Redirecciona a la página de login tras el registro
    }

    // Procesa el login del usuario
    @PostMapping("/login")
    public String login(@RequestParam String username_mail,
            @RequestParam String password) {
        userService.login(username_mail, password); // Llama al método login del servicio
        return "redirect:/";
    }

    // !NO BORRAR LO ESTOY INTENTANDO HACER
    // // Procesa el login del usuario
    // @PostMapping("/login")
    // public String login(@RequestParam String username_mail,
    // @RequestParam String password,
    // HttpSession session) {
    // Optional<User> userOptional = userService.login(username_mail, password);
    // if (userOptional.isPresent()) {
    // // Guarda el nombre en la sesión para mostrarlo en la vista
    // session.setAttribute("loggedUsername", userOptional.get().getUsername());
    // }
    // return "redirect:/";
    // }
}