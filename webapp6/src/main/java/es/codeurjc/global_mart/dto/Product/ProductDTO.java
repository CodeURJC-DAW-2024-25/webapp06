package es.codeurjc.global_mart.dto.Product;

import java.util.List;

import com.mysql.cj.jdbc.Blob;

import es.codeurjc.global_mart.dto.Reviewss.ReviewDTO;

public record ProductDTO(
    Long id,
    String type, 
    String name,
    String company,
    Double price,
    String description,
    Integer views_count,
    Boolean isAccepted,
    Blob image,
    List<ReviewDTO> reviews,
    Integer stock ) {
}


