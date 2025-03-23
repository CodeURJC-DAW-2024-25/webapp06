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

import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.User.ShoppingCartDTO;
import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.service.OrderService;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;

@Controller
public class ShoppingCartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/shoppingcart")
    public String shoppingCart(Model model, Authentication autentication) {

        Object principal = autentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            model.addAttribute("username", oAuth2User.getAttribute("name"));
            UserDTO user = userService.findByUsername(oAuth2User.getAttribute("name")).get();

            ShoppingCartDTO shoppingCart = userService.getShoppingCartData(user);
            productService.addImageDataToProducts(shoppingCart.cartProducts());
            model.addAttribute("cart", shoppingCart);
            model.addAttribute("isEmpty", 0);

        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            Optional<UserDTO> user = userService.findByUsername(userDetails.getUsername());
            if (user.isPresent()) {
                model.addAttribute("username", user.get().username());
                // UserDTO u = userService.findByUsername(user.get().username()).get();

                ShoppingCartDTO shoppingCart = userService.getShoppingCartData(user.get());
                productService.addImageDataToProducts(shoppingCart.cartProducts());
                model.addAttribute("cart", shoppingCart);
                model.addAttribute("isEmpty", 0);
            }
        }

        return "shoppingcart";
    }

    // adds a product to the user cart
    @PostMapping("/shoppingcart/{productId}")
    public String addProductToCart(@PathVariable Long productId, Authentication autentication) {
        Object principal = autentication.getPrincipal();
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

        return "redirect:/shoppingcart";
    }

    // removes a product from the user cart
    @PostMapping("/removeProductFromCart/{id}")
    public String removeProductFromCart(@PathVariable Long id, Authentication autentication) {
        Object principal = autentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            UserDTO user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ProductDTO product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (userService.productInCart(user, product)) {
                userService.removeProductFromCart(user, product); // call the user method to remove

            }
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            UserDTO user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            ProductDTO product = productService.getProductById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (userService.productInCart(user, product)) {
                userService.removeProductFromCart(user, product);
            }
        } else {
            System.out.println("No user found");
            return "redirect:/";
        }

        return "redirect:/shoppingcart";
    }

    // to place an order
    @PostMapping("/payment")
    public String payment(Authentication autentication) {
        Object principal = autentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            UserDTO user = userService.findByUsername(oAuth2User.getAttribute("name"))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            orderService.createOrder(user); // all the payment logic is in the orderService
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            UserDTO user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            orderService.createOrder(user); // all the payment logic is in the orderService
        } else {
            System.out.println("No furula");
        }
        return "redirect:/";
    }
}

// private void addImageDataToProducts(List<ProductDTO> products) {

// userService.addImageDataToProducts(products);
// for (Product product : products) {
// try {
// Blob imageBlob = product.getImage();
// if (imageBlob != null) {
// byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
// String imageBase64 = "data:image/jpeg;base64," +
// Base64.getEncoder().encodeToString(bytes);
// product.setImageBase64(imageBase64); // Necesitas añadir este campo a la
// clase Product
// }
// } catch (Exception e) {
// e.printStackTrace();
// }
// }
// }

// }
