package es.codeurjc.global_mart.controller;

import es.codeurjc.global_mart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import es.codeurjc.global_mart.model.User;

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

    // Procesa el registro del usuario
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, // Recibe los datos del formulario
            @RequestParam String mail,
            @RequestParam String password,
            @RequestParam String role,
            HttpServletRequest request, Model model) {
        // Imprime en consola para comprobar los datos
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", token.getToken());
        logger.info("Registro recibido - username: {}, email: {}, role: {}", username, mail, role);
        userService.createUser(username, mail, password, List.of(role)); // Llama al método createUser del servicio

        return "redirect:/"; // Redirecciona a la página de login tras el registro
    }

    // Procesa el login del usuario
    @PostMapping("/login")
    public String login(@RequestParam String username_mail,
            @RequestParam String password, HttpServletRequest request, Model model) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        model.addAttribute("token", token.getToken());
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