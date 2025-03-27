package es.codeurjc.global_mart.controller.API_Rest.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.global_mart.security.jwt.AuthResponse;
import es.codeurjc.global_mart.security.jwt.AuthResponse.Status;
import es.codeurjc.global_mart.security.jwt.LoginRequest;
import es.codeurjc.global_mart.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private UserLoginService userService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        boolean loginSuccess = userService.login(response, loginRequest);

        if (loginSuccess) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken, 
            HttpServletResponse response) {
        
        boolean isRefreshed = userService.refresh(response, refreshToken);
        
        if (isRefreshed) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
    }
    

    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(HttpServletResponse response) {
        boolean logoutSuccessful = userService.logout(response);

        if (logoutSuccessful) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Login endpoint is accessible");
    }
}