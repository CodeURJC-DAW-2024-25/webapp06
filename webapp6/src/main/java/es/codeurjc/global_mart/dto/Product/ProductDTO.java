package es.codeurjc.global_mart.dto.Product;

public record ProductDTO(
    Long id,
    String type, 
    String name,
    String company,
    Double price,
    String description,
    Integer views_count,
    Boolean isAccepted) {
}
