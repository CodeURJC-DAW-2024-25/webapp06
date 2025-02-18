package es.codeurjc.global_mart.config;

import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import es.codeurjc.global_mart.service.ReviewService;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DataLoader {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ReviewService reviewService;
    

    @PostConstruct
    public void loadData() {
<<<<<<< HEAD
        productService.createProduct("Libros", "Producto1", "Amazon", 20.0, "Muy chulo", "String image");
        productService.createProduct("Electronica", "Producto2", "eBay", 30.0, "Muy útil", "String image");
        productService.createProduct("Electronica", "Producto3", "Walmart", 40.0, "Muy práctico", "String image");
        productService.createProduct("Deporte", "Producto4", "Decathlon", 50.0, "Muy resistente", "String image");
        productService.createProduct("Musica", "Producto5", "Zara", 60.0, "Muy elegante", "String image");
=======

        userService.createUser("user1", "", "");

        reviewService.createReview("user1", "Muy bueno", 5);

        productService.createProduct("Hogar", "Producto1", "Amazon", 20.0, "Muy chulo", "String image");
        productService.createProduct("Electrónica", "Producto2", "eBay", 30.0, "Muy útil", "String image");
        productService.createProduct("Jardinería", "Producto3", "Walmart", 40.0, "Muy práctico", "String image");
        productService.createProduct("Deportes", "Producto4", "Decathlon", 50.0, "Muy resistente", "String image");
        productService.createProduct("Moda", "Producto5", "Zara", 60.0, "Muy elegante", "String image");
>>>>>>> 67df06f2d27948ddcf1094ad78219d0f0ef0dd6f
    }
}