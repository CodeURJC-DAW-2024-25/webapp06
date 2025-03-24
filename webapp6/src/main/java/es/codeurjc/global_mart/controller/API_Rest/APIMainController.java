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
    public ResponseEntity<List<Product>> getMostViewedProducts() {
        List<ProductDTO> mostViewedProducts = productService.getMostViewedProducts(4);
        addImageDataToProducts(mostViewedProducts);
        List<Product> products = productMapper.toProducts(mostViewedProducts);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/lastProducts")
    public ResponseEntity<List<Product>> getLastProducts() {
        List<ProductDTO> lastProducts = productService.getLastProducts();
        addImageDataToProducts(lastProducts);
        List<Product> products = productMapper.toProducts(lastProducts);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/acceptedProducts")
    public ResponseEntity<List<Product>> getAcceptedProducts() {
        List<ProductDTO> acceptedProducts = productService.getAcceptedProducts();
        addImageDataToProducts(acceptedProducts);
        List<Product> products = productMapper.toProducts(acceptedProducts);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/acceptedProductsByType/{type}")
    public ResponseEntity<List<Product>> getAcceptedProductsByType(@PathVariable String type) {
        List<ProductDTO> acceptedProductsByType = productService.getAcceptedProductsByType(type);
        addImageDataToProducts(acceptedProductsByType);
        List<Product> products = productMapper.toProducts(acceptedProductsByType);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/acceptedCompanyProducts")
    public ResponseEntity<List<Product>> getAcceptedCompanyProducts(Authentication authentication) {
        UserProfile userProfile = (UserProfile) getProfile(authentication).getBody();
        if (userProfile == null) {
            return ResponseEntity.status(401).body(null);
        }
        String company = userProfile.username;
        List<ProductDTO> acceptedCompanyProducts = productService.getAcceptedCompanyProducts(company);
        addImageDataToProducts(acceptedCompanyProducts);
        List<Product> products = productMapper.toProducts(acceptedCompanyProducts);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<ProductDTO> productObtained = productService.getProductById(id);
        if (productObtained.isPresent()) {
            ProductDTO productDTO = productObtained.get();
            addImageDataToProduct(productDTO);
            Product product = productMapper.toProduct(productDTO);
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<List<Product>> loadMoreProducts(@RequestParam int page) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<ProductDTO> productsPage = productService.getAcceptedProducts(pageable);
        List<ProductDTO> products = productsPage.getContent();
        addImageDataToProducts(products);
        List<Product> productsThrow = productMapper.toProducts(products);
        return ResponseEntity.ok(productsThrow);
    }

    @GetMapping("/moreProdsTypes/{type}")
    public ResponseEntity<List<Product>> loadMoreProductsByType(@RequestParam int page, @PathVariable String type) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<ProductDTO> productsPage = productService.getAcceptedProductsByType(type, pageable);
        List<ProductDTO> products = productsPage.getContent();
        addImageDataToProducts(products);
        List<Product> productsThrow = productMapper.toProducts(products);
        return ResponseEntity.ok(productsThrow);
    }

    @GetMapping("/moreProdsCompany")
    public ResponseEntity<List<Product>> loadMoreProductsByCompany(@RequestParam int page,
            @RequestParam String company) {
        Pageable pageable = Pageable.ofSize(5).withPage(page);
        Page<ProductDTO> productsPage = productService.getAcceptedCompanyProducts(company, pageable);
        List<ProductDTO> products = productsPage.getContent();
        addImageDataToProducts(products);
        List<Product> productsThrow = productMapper.toProducts(products);
        return ResponseEntity.ok(productsThrow);
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
