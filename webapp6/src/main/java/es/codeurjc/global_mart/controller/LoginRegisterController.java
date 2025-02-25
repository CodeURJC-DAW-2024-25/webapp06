package es.codeurjc.global_mart.controller;

import es.codeurjc.global_mart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.model.LoggedUser;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller; // Asegurarse de importar Controller
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(LoginRegisterController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LoggedUser loggedUser;

    // Procesa el registro del usuario
    @PostMapping("/register")
    public String registerUser(@RequestParam String name, @RequestParam String username, // Recibe los datos del
                                                                                         // formulario
            @RequestParam String mail,
            @RequestParam String password,
            @RequestParam String image,
            @RequestParam String role,
            HttpServletRequest request, Model model) {
        userService.createUser(image, name, username, mail, password, List.of(role)); // Llama al método createUser del
                                                                                      // servicio

        return "redirect:/"; // Redirecciona a la página de login tras el registro
    }

    // Procesa el login del usuario
    @PostMapping("/login")
    public String login(@RequestParam String username,
            @RequestParam String password, HttpServletRequest request, Model model) {
        Optional<User> userOpt = userService.login(username, password); // Llama al método login del servicio
        if (userOpt.isPresent()) {
            loggedUser.setUser(userOpt.get());
            return "redirect:/";
        } else {
            logger.warn("Login fallido para el usuario: {}", username);
            return "redirect:/login_error";
        }
    }
}