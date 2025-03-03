package es.codeurjc.global_mart.service;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.Review;
import es.codeurjc.global_mart.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewRepository reviewRepository;

    // Crear una nueva reseña
    public Review createReview(String username, String comment, int calification) {
        Review review = new Review(username, comment, calification);
        return reviewRepository.save(review);
    }

    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    // Obtener todas las reseñas
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // Obtener una reseña por su ID
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    // Actualizar una reseña existente
    public Review updateReview(Long id, String comment, int calification) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setComment(comment);
            review.setCalification(calification);
            return reviewRepository.save(review);
        } else {
            throw new RuntimeException("Review not found with id " + id);
        }
    }

    // Eliminar una reseña por su ID
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

}