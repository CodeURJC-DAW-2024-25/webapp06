package es.codeurjc.global_mart.controller.apirest.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.global_mart.security.jwt.LoginRequest;
import es.codeurjc.global_mart.security.jwt.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/v1/api/auth")
public class LoginController {

    @Autowired
    private UserLoginService userService;


    @Operation(summary = "Login a user", description = "Authenticate a user and provide a JWT token upon successful login.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Login successful, no content returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, invalid credentials")
    })
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


    @Operation(summary = "Refresh authentication token", description = "Refresh the user's authentication token using the refresh token stored in the cookie.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Refresh successful, new token issued"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, invalid or expired refresh token")
    })
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


    @Operation(summary = "Logout a user", description = "Logout the user by invalidating the session and refresh token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Logout successful, no content returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized, unable to logout")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logOut(HttpServletResponse response) {
        boolean logoutSuccessful = userService.logout(response);

        if (logoutSuccessful) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
    }


    @Operation(summary = "Test login endpoint", description = "Check if the login endpoint is accessible.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Test endpoint accessible")
    })
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Login endpoint is accessible");
    }
}