package es.codeurjc.global_mart.dto.User;

import java.util.List;
import java.sql.Blob;

import es.codeurjc.global_mart.dto.Orders.OrderDTO;
import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.Reviewss.ReviewDTO;

public record UserDTO(
        String name,
        String username,
        String email,
        Blob image,
        List<String> role,
        List<OrderDTO> orders,
        List<ReviewDTO> reviews,
        List<ProductDTO> cart,
        Double cartPrice,
        List<Double> historicalOrderPrices,
        String imageBase64) {
}
