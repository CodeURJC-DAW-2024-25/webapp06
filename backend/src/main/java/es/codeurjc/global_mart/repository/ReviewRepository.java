package es.codeurjc.global_mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.codeurjc.global_mart.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
}