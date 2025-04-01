package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import es.codeurjc.global_mart.dto.User.UserDTO;

import java.util.Optional;

@RestController
@RequestMapping("/api/main")
public class APIMainController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(null);
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            // Para usuarios OAuth2, necesitamos buscar o crear el usuario en nuestra base
            // de datos
            Optional<UserDTO> existingUser = userService.findByUsername(oAuth2User.getAttribute("name"));

            if (existingUser.isPresent()) {
                return ResponseEntity.ok(existingUser.get());
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            Optional<UserDTO> user = userService.findByUsername(userDetails.getUsername());
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            }
        }

        return ResponseEntity.status(404).body(null);
    }
}