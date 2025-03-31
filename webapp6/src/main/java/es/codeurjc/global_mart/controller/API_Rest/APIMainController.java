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

    private static class UserProfile {
        public String name;
        public String username;
        public String email;
        public String profileImage;
        public boolean isGoogleUser;

        public UserProfile(String name, String username, String email, String profileImage, boolean isGoogleUser) {
            this.name = name;
            this.username = username;
            this.email = email;
            this.profileImage = profileImage;
            this.isGoogleUser = isGoogleUser;
        }
    }

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(null);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            return ResponseEntity.ok(new UserProfile(
                    oAuth2User.getAttribute("name"),
                    oAuth2User.getAttribute("name"),
                    oAuth2User.getAttribute("email"),
                    oAuth2User.getAttribute("picture"),
                    true));
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            Optional<UserDTO> user = userService.findByUsername(userDetails.getUsername());
            if (user.isPresent()) {
                UserDTO u = user.get();
                return ResponseEntity.ok(new UserProfile(
                        u.name(),
                        u.username(),
                        u.email(),
                        null, // review this
                        false));
            }
        }

        return ResponseEntity.status(404).body(null);
    }

}
