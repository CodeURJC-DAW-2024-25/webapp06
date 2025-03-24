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
import es.codeurjc.global_mart.dto.User.UserDTO;
import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.Product.ProductMapper;

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
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("/mostViewedProducts")
    public ResponseEntity<List<ProductDTO>> getMostViewedProducts() {
        List<ProductDTO> mostViewedProducts = productService.getMostViewedProducts(4);
        return ResponseEntity.ok(mostViewedProducts);
    }

    @GetMapping("/lastProducts")
    public ResponseEntity<List<Product>> getLastProducts() {
        List<ProductDTO> lastProducts = productService.getLastProducts();
        addImageDataToProducts(lastProducts);
        List<Product> products = productMapper.toProducts(lastProducts);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/acceptedProducts")
    public ResponseEntity<List<ProductDTO>> getAcceptedProducts() {
        List<ProductDTO> acceptedProducts = productService.getAcceptedProducts();
        return ResponseEntity.ok(acceptedProducts);
    }

    @GetMapping("/acceptedProductsByType/{type}")
    public ResponseEntity<List<ProductDTO>> getAcceptedProductsByType(@PathVariable String type) {
        List<ProductDTO> acceptedProductsByType = productService.getAcceptedProductsByType(type);
        return ResponseEntity.ok(acceptedProductsByType);
    }

    @GetMapping("/acceptedCompanyProducts")
    public ResponseEntity<List<ProductDTO>> getAcceptedCompanyProducts(Authentication authentication) {
        UserProfile userProfile = (UserProfile) getProfile(authentication).getBody();
        if (userProfile == null) {
            return ResponseEntity.status(401).body(null);
        }
        String company = userProfile.username;
        List<ProductDTO> acceptedCompanyProducts = productService.getAcceptedCompanyProducts(company);
        return ResponseEntity.ok(acceptedCompanyProducts);
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
                    true));
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            Optional<UserDTO> user = userService.findByUsername(userDetails.getUsername());
            if (user.isPresent()) {
                UserDTO u = user.get();
                return ResponseEntity.ok(new UserProfile(
                        u.name(),
                        u.username(),
                        u.email(),
                        null, // reviw this
                        false));
            }
        }

        return ResponseEntity.status(404).body("User not found");
    }

//Not working the ajax, it doesnt find the url

    @GetMapping("/moreProdsAll")
    public ResponseEntity<List<ProductDTO>> loadMoreProducts(@RequestParam int page) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<ProductDTO> productsPage = productService.getAcceptedProducts(pageable);
        List<ProductDTO> products = productsPage.getContent();
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/moreProdsTypes/{type}")
    public ResponseEntity<List<ProductDTO>> loadMoreProductsByType(@RequestParam int page, @PathVariable String type) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<ProductDTO> productsPage = productService.getAcceptedProductsByType(type, pageable);
        List<ProductDTO> products = productsPage.getContent();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/moreProdsCompany")
    public ResponseEntity<List<ProductDTO>> loadMoreProductsByCompany(@RequestParam int page,
            @RequestParam String company) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<ProductDTO> productsPage = productService.getAcceptedCompanyProducts(company, pageable);
        List<ProductDTO> products = productsPage.getContent();
        return ResponseEntity.ok(products);
    }

    private void addImageDataToProducts(List<ProductDTO> products) {
        for (ProductDTO product : products) {
            addImageDataToProduct(product);
        }
    }

    private void addImageDataToProduct(ProductDTO productDTO) {
        try {
            Product product = productMapper.toProduct(productDTO);
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
