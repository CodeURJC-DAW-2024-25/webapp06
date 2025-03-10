package es.codeurjc.global_mart.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.UserService;

@ControllerAdvice
public class BaseController {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addUserAttributes(Authentication authentication, org.springframework.ui.Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            model.addAttribute("logged", true);

            // OAuth2 user (Google, GitHub, etc.)
            if (principal instanceof OAuth2User oAuth2User) {
                model.addAttribute("username", oAuth2User.getAttribute("name"));
                model.addAttribute("email", oAuth2User.getAttribute("email"));
                model.addAttribute("profile_image", oAuth2User.getAttribute("picture"));
                model.addAttribute("isUser", true);
                model.addAttribute("isAdmin", false);
                model.addAttribute("isCompany", false);
            }
            // Regular user
            else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
                Optional<User> user = userService.findByUsername(userDetails.getUsername());
                if (user.isPresent()) {
                    model.addAttribute("username", user.get().getUsername());
                    model.addAttribute("email", user.get().getEmail());
                    model.addAttribute("profile_image", user.get().getImage());

                    if (user.get().isAdmin()) {
                        model.addAttribute("isAdmin", true);
                        model.addAttribute("isCompany", false);
                        model.addAttribute("isUser", false);
                    } else if (user.get().isCompany()) {
                        model.addAttribute("isAdmin", false);
                        model.addAttribute("isCompany", true);
                        model.addAttribute("isUser", false);
                    } else {
                        model.addAttribute("isAdmin", false);
                        model.addAttribute("isCompany", false);
                        model.addAttribute("isUser", true);
                    }
                }
            }
        } else {
            model.addAttribute("logged", false);
            model.addAttribute("isAdmin", false);
            model.addAttribute("isCompany", false);
            model.addAttribute("isUser", false);
        }
    }
}