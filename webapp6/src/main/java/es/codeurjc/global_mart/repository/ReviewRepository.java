package es.codeurjc.global_mart.repository;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.Review;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct(Product product);
    
}