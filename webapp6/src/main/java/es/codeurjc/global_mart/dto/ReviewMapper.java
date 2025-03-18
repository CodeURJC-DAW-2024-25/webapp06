package es.codeurjc.global_mart.dto;

import org.mapstruct.Mapper;

import es.codeurjc.global_mart.model.Review;

@Mapper (componentModel = "spring")
public interface ReviewMapper {

    ReviewDTO toReviewDTO(Review review);
    
}