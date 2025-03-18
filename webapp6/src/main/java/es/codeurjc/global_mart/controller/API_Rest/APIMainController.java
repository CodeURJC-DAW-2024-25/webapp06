package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/main")
public class APIMainController {

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
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/mostViewedProducts")
    public ResponseEntity<List<Product>> getMostViewedProducts(@RequestParam int limit) {
        List<Product> mostViewedProducts = productService.getMostViewedProducts(limit);
        addImageDataToProducts(mostViewedProducts);
        return ResponseEntity.ok(mostViewedProducts);
    }

    @GetMapping("/lastProducts")
    public ResponseEntity<List<Product>> getLastProducts(@RequestParam int limit) {
        List<Product> lastProducts = productService.getLastProducts(limit);
        addImageDataToProducts(lastProducts);
        return ResponseEntity.ok(lastProducts);
    }

    @GetMapping("/acceptedProducts")
    public ResponseEntity<List<Product>> getAcceptedProducts() {
        List<Product> acceptedProducts = productService.getAcceptedProducts();
        addImageDataToProducts(acceptedProducts);
        return ResponseEntity.ok(acceptedProducts);
    }

    @GetMapping("/acceptedProductsByType")
    public ResponseEntity<List<Product>> getAcceptedProductsByType(@RequestParam String type) {
        List<Product> acceptedProductsByType = productService.getAcceptedProductsByType(type);
        addImageDataToProducts(acceptedProductsByType);
        return ResponseEntity.ok(acceptedProductsByType);
    }

    @GetMapping("/acceptedCompanyProducts")
    public ResponseEntity<List<Product>> getAcceptedCompanyProducts(@RequestParam String company) {
        List<Product> acceptedCompanyProducts = productService.getAcceptedCompanyProducts(company);
        addImageDataToProducts(acceptedCompanyProducts);
        return ResponseEntity.ok(acceptedCompanyProducts);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> {
                    addImageDataToProduct(product);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            return ResponseEntity.ok(new UserProfile(
                    oAuth2User.getAttribute("name"),
                    oAuth2User.getAttribute("name"),
                    oAuth2User.getAttribute("email"),
                    oAuth2User.getAttribute("picture"),
                    true
            ));
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            Optional<es.codeurjc.global_mart.model.User> user = userService.findByUsername(userDetails.getUsername());
            if (user.isPresent()) {
                es.codeurjc.global_mart.model.User u = user.get();
                return ResponseEntity.ok(new UserProfile(
                        u.getName(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getImageBase64(),
                        false
                ));
            }
        }

        return ResponseEntity.status(404).body("User not found");
    }

    @GetMapping("/moreProdsAll")
    public ResponseEntity<List<Product>> loadMoreProducts(@RequestParam int page) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<Product> productsPage = productService.getAcceptedProducts(pageable);
        List<Product> products = productsPage.getContent();
        addImageDataToProducts(products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/moreProdsTypes")
    public ResponseEntity<List<Product>> loadMoreProductsByType(@RequestParam int page, @RequestParam String type) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<Product> productsPage = productService.getAcceptedProductsByType(type, pageable);
        List<Product> products = productsPage.getContent();
        addImageDataToProducts(products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/moreProdsCompany")
    public ResponseEntity<List<Product>> loadMoreProductsByCompany(@RequestParam int page, @RequestParam String company) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<Product> productsPage = productService.getAcceptedCompanyProducts(company, pageable);
        List<Product> products = productsPage.getContent();
        addImageDataToProducts(products);
        return ResponseEntity.ok(products);
    }

    private void addImageDataToProducts(List<Product> products) {
        for (Product product : products) {
            addImageDataToProduct(product);
        }
    }

    private void addImageDataToProduct(Product product) {
        try {
            Blob imageBlob = product.getImage();
            if (imageBlob != null) {
                byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
                String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
                product.setImageBase64(imageBase64);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
