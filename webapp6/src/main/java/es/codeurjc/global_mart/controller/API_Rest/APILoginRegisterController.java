package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class APILoginRegisterController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/newUser")
    public ResponseEntity<?> registerUser(
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String mail,
            @RequestParam String password,
            @RequestParam MultipartFile image,
            @RequestParam String role) throws Exception {

        Optional<UserDTO> user = userService.findByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        } else {
            userService.createUser(image, name, username, mail, passwordEncoder.encode(password), List.of(role));
        }

        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/loginComprobation")
    public ResponseEntity<?> loginComprobation(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Optional<UserDTO> existingUser = userService.findByEmail(email);

        if (existingUser.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(oAuth2User.getAttribute("name"));
            newUser.setEmail(email);
            // newUser.setImage(oAuth2User.getAttribute("picture"));
            newUser.setRole(List.of("USER")); // we set the role to USER

            userService.save(newUser);
        }

        return ResponseEntity.ok("Login comprobation successful");
    }
}