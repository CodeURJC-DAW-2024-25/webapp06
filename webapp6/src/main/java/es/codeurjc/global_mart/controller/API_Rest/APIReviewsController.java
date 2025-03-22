package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.Reviewss.ReviewDTO;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class APIReviewsController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ProductService productService;

    @PostMapping("/product/{id}/new_review")
    public ResponseEntity<?> postNewReview(
            @RequestParam int calification,
            @RequestParam String comment,
            Authentication authentication,
            @PathVariable Long id) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<ProductDTO> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found");
        }

        Object principal = authentication.getPrincipal();
        ReviewDTO review = null;

        if (principal instanceof OAuth2User oAuth2User) {
            String username = oAuth2User.getAttribute("name");
            review = reviewService.createReview(username, comment, calification);
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            String username = userDetails.getUsername();
            review = reviewService.createReview(username, comment, calification);
        }

        if (review != null) {
            productService.addReviewToProduct(product.get(), review);
            productService.addProduct(product.get());
            return ResponseEntity.ok("Review added successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to create review");
        }
    }
}
