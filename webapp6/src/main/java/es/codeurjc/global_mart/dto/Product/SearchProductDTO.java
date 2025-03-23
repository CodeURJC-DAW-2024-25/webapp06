package es.codeurjc.global_mart.dto.Product;

import java.sql.Blob;

public record SearchProductDTO(
                // Blob image,
                String name,
                Double price,
                String type) {
}
