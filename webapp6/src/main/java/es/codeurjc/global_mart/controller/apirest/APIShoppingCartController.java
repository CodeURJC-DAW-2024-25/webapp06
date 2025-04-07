package es.codeurjc.global_mart.controller.apirest;

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
@RequestMapping("/v1/api/users")
public class APIShoppingCartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/{id}/shoppingcarts")
    public ResponseEntity<?> getShoppingCart(@PathVariable Long id, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(null);
        }

        UserDTO user = getUserFromAuthentication(authentication);

        if (user != null && userService.getUserId(user) == id) {
            ShoppingCartDTO shoppingCart = userService.getShoppingCartData(user);
            productService.addImageDataToProducts(shoppingCart.cartProducts());
            return ResponseEntity.ok(shoppingCart);
        }

        return ResponseEntity.status(404).body(null);

    }

    @PostMapping("/{id}/shoppingcarts/{productId}")
    public ResponseEntity<ProductDTO> addProductToCart(@PathVariable Long id, @PathVariable Long productId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(null);
        }

        UserDTO user = getUserFromAuthentication(authentication);

        System.out.println("User Id: " + userService.getUserId(user));
        System.out.println("User Id by path: " + id);

        if (user != null && userService.getUserId(user) == id) {
            ProductDTO product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            userService.addProductToCart(user, product);
            return ResponseEntity.ok(product);
        }

        return ResponseEntity.status(404).body(null);
    }

    @DeleteMapping("/{id}/shoppingcarts/{productId}")
    public ResponseEntity<ProductDTO> removeProductFromCart(@PathVariable Long productId,
            Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body(null);
        }

        UserDTO user = getUserFromAuthentication(authentication);

        ProductDTO product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (userService.productInCart(user, product)) {
            userService.removeProductFromCart(user, product);
        }

        return ResponseEntity.ok(product);
    }

    @PostMapping("/{id}/shoppingcarts/payment")
    public ResponseEntity<?> payment(Authentication authentication) {

        UserDTO user = getUserFromAuthentication(authentication);

        orderService.createOrder(user);

        return ResponseEntity.ok(null);
    }

    private UserDTO getUserFromAuthentication(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            return userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            return userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        return null;
    }
}
