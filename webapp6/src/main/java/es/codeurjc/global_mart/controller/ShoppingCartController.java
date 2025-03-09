package es.codeurjc.global_mart.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.OrderService;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;

@Controller // Añadido @Controller
public class ShoppingCartController {

    @Autowired // Añadido @Autowired
    private UserService userService;

    @Autowired // Añadido @Autowired
    private ProductService productService;

    @Autowired // Añadido @Autowired
    private OrderService orderService;

    @GetMapping("/shoppingcart")
    public String shoppingCart(Model model, Authentication autentication) {

        Object principal = autentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            model.addAttribute("username", oAuth2User.getAttribute("name"));
            model.addAttribute("products",
                    userService.getCartProducts(userService.findByUsername(oAuth2User.getAttribute("name")).get()));
            model.addAttribute("totalPrice",
                    userService.getTotalPrice(userService.findByUsername(oAuth2User.getAttribute("name")).get()));
            // org.springframework.security.web.csrf.CsrfToken csrfToken =
            // CSRFHandlerConfiguration.getToken();
            // model.addAttribute("token", csrfToken.getToken());
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            Optional<User> user = userService.findByUsername(userDetails.getUsername());
            if (user.isPresent()) {
                model.addAttribute("username", user.get().getUsername());
                model.addAttribute("products", userService.getCartProducts(user.get()));
                model.addAttribute("totalPrice", userService.getTotalPrice(user.get()));
            }
        }

        return "shoppingcart";
    }

    @PostMapping("/shoppingcart/{productId}")
    public String addProductToCart(@PathVariable Long productId, Authentication autentication) {
        Object principal = autentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            User user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            userService.addProductToCart(user, product);
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            userService.addProductToCart(user, product);
        }

        return "redirect:/shoppingcart";
    }

    // removes a product from the user cart
    @PostMapping("/removeProductFromCart/{id}")
    public String removeProductFromCart(@PathVariable Long id, Authentication autentication) {
        Object principal = autentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            User user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (user.getCart().contains(product)) {
                userService.removeProductFromCart(user, product); // call the user method to remove
                // userService.save( user); // update the user in DDBB without this line the
                // user continues rendering the product

            }
        } else {
            return "redirect:/";
        }

        return "redirect:/shoppingcart";
    }

    // to place an order
    @PostMapping("/payment")
    public String payment(Authentication autentication) {
        Object principal = autentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            User user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            orderService.createOrder(user); // all the payment logic is in the orderService
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            orderService.createOrder(user); // all the payment logic is in the orderService
        }
        return "redirect:/";
    }
}
