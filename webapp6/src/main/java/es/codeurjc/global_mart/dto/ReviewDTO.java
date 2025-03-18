package es.codeurjc.global_mart.dto;

import java.time.LocalDate;

public record ReviewDTO(
    Long reviewId,
    String username, 
    String comment,
    int calification,
    LocalDate creationDate) {
    
}



