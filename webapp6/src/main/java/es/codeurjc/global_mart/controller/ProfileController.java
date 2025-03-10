package es.codeurjc.global_mart.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.web.csrf.CsrfToken;

import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.security.RepositoryUserDetailsService;
import es.codeurjc.global_mart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    @GetMapping("/edit_profile")
    public String showEditProfile(Model model, Authentication authentication, HttpServletRequest request) {
        if (authentication == null) {
            return "redirect:/login";
        }

        User user = getUserFromAuthentication(authentication);
        if (user == null) {
            return "redirect:/";
        }

        // Añadir datos del usuario al modelo
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("name", user.getName());
        model.addAttribute("userId", user.getId());

        // Añadir token CSRF al modelo
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        if (token != null) {
            model.addAttribute("token", token.getToken());
        }

        return "edit_profile";
    }

    @PostMapping("/edit_profile")
    public String updateProfile(
            @RequestParam("userId") Long userId,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("name") String name,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            Authentication currentAuth) {

        Optional<User> optionalUser = userService.getUserById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Guardar el nombre de usuario antiguo
            String oldUsername = user.getUsername();

            // Actualizar datos básicos
            user.setUsername(username);
            user.setEmail(email);
            user.setName(name);

            // Actualizar contraseña solo si se proporciona y coincide con la confirmación
            if (password != null && !password.isEmpty() && password.equals(confirmPassword)) {
                user.setPassword(passwordEncoder.encode(password));
            }

            // Guardar los cambios
            userService.save(user);

            // Si el nombre de usuario cambió, actualizar la autenticación en el contexto de
            // seguridad
            if (!oldUsername.equals(username) && currentAuth instanceof UsernamePasswordAuthenticationToken) {
                // Cargar los nuevos detalles del usuario
                UserDetails newUserDetails = userDetailsService.loadUserByUsername(username);

                // Crear una nueva autenticación con los nuevos detalles
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                        newUserDetails, null, newUserDetails.getAuthorities());

                // Actualizar el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }

            return "redirect:/profile";
        } else {
            // Usuario no encontrado
            return "redirect:/";
        }
    }

    private User getUserFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            Optional<User> user = userService.findByEmail(email);
            return user.orElse(null);
        } else if (principal instanceof UserDetails userDetails) {
            Optional<User> user = userService.findByUsername(userDetails.getUsername());
            return user.orElse(null);
        }

        return null;
    }
}