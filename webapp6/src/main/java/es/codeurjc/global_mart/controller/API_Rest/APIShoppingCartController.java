package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.User.ShoppingCartDTO;
import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.service.OrderService;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shoppingCarts")
public class APIShoppingCartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public ResponseEntity<?> getShoppingCart(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            UserDTO user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ShoppingCartDTO shoppingCart = userService.getShoppingCartData(user);
            productService.addImageDataToProducts(shoppingCart.cartProducts());
            return ResponseEntity.ok(shoppingCart);
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            UserDTO user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ShoppingCartDTO shoppingCart = userService.getShoppingCartData(user);
            productService.addImageDataToProducts(shoppingCart.cartProducts());
            return ResponseEntity.ok(shoppingCart);
        }

        return ResponseEntity.status(404).body("User not found");
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addProductToCart(@PathVariable Long productId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            UserDTO user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ProductDTO product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            userService.addProductToCart(user, product);
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            UserDTO user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ProductDTO product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            userService.addProductToCart(user, product);
        }

        return ResponseEntity.ok("Product added to cart");
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable Long productId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            UserDTO user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ProductDTO product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (userService.productInCart(user, product)) {
                userService.removeProductFromCart(user, product);
            }
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            UserDTO user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ProductDTO product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (userService.productInCart(user, product)) {
                userService.removeProductFromCart(user, product);
            }
        }

        return ResponseEntity.ok("Product removed from cart");
    }

    @PostMapping("/payment")
    public ResponseEntity<?> payment(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            UserDTO user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            orderService.createOrder(user);
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            UserDTO user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            orderService.createOrder(user);
        }

        return ResponseEntity.ok("Order placed successfully");
    }
}
