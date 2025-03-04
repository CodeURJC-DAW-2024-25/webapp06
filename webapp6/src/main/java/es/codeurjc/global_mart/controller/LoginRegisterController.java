package es.codeurjc.global_mart.controller;

import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Asegurarse de importar Controller
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;


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


        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            return "errors";
        }else{
            userService.createUser(image, name, username, mail, passwordEncoder.encode(password), List.of(role));
        }
        

        return "redirect:/"; // Redirecciona a la página de login tras el registro
    }

    @GetMapping("/loginComprobation")
public String loginComprobation(OAuth2AuthenticationToken authentication) {
    OAuth2User oAuth2User = authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");

    Optional<User> existingUser = userService.findByEmail(email);

    if (existingUser.isEmpty()) {
        User newUser = new User();
        newUser.setUsername(oAuth2User.getAttribute("name"));
        newUser.setEmail(email);
        //newUser.setImage(oAuth2User.getAttribute("picture"));
        newUser.setRole(List.of("USER")); // Por defecto, asignamos el rol de usuario normal

        userService.save(newUser);
    }

    return "redirect:/"; // Redirige a la página de perfil
}

    
}