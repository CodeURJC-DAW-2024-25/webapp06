package es.codeurjc.global_mart.dto;

import java.util.List;

public record OrderDTO(
    Long id,
    Double total,
    List<ProductDTO> products,
    String userName) {
}
