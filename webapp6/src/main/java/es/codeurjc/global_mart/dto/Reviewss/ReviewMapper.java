package es.codeurjc.global_mart.dto.Reviewss;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.global_mart.model.Review;

@Mapper (componentModel = "spring")
public interface ReviewMapper {

    ReviewDTO toReviewDTO(Review review);

    List<ReviewDTO> toReviewsDTO(Collection<Review> reviews);
    
}