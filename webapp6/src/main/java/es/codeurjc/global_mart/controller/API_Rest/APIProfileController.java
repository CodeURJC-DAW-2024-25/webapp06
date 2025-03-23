package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.dto.User.UserMapper;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class APIProfileController {

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

    @Autowired
    private UserMapper userMapper;

    @PutMapping
    public ResponseEntity<?> updateProfile(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestParam(value = "password", required = false) String password,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            Optional<UserDTO> optionalUser = userService.findByUsername(userDetails.getUsername());

            if (optionalUser.isPresent()) {
                UserDTO userDTO = optionalUser.get();
                User user = userMapper.toUser(userDTO);

                user.setUsername(username);
                user.setEmail(email);
                user.setName(name);

                if (password != null && !password.isEmpty()) {
                    user.setPassword(password); // Assuming password encoding is handled in the service layer
                }

                userService.save(user);
                return ResponseEntity.ok("Profile updated successfully");
            }
        }

        return ResponseEntity.status(404).body("User not found");
    }

}