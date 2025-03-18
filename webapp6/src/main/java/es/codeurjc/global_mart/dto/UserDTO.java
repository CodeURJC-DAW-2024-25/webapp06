package es.codeurjc.global_mart.dto;

import java.util.List;
import es.codeurjc.global_mart.model.Order;
import es.codeurjc.global_mart.model.Review;
import es.codeurjc.global_mart.model.Product;


public record UserDTO(
    String name, 
    String username,
    String email,
    List<String> role,
    List<OrderDTO> orders,
    List<ReviewDTO> reviews,  
    List<ProductDTO> cart,
    double cartPrice,
    List<Double> historicalOrderPrices) {
}