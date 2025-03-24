package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.dto.User.UserCreationDTO;
import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.dto.User.UserMapper;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class APIUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody UserCreationDTO userDto) {
        Optional<UserDTO> user = userService.findByUsername(userDto.username());
        if (user.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        } else {
            try {
                userService.createUser(null, userDto.name(), userDto.username(), userDto.email(), passwordEncoder.encode(userDto.password()), userDto.role());
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Error creating user: " + e.getMessage());
            }
        }
    
        return ResponseEntity.ok("User registered successfully");
    }
    
 
    @PutMapping("/profile")
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