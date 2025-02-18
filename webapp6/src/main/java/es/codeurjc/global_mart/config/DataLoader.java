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

        productService.createProduct("Libros", "Producto1", "Amazon", 20.0, "Muy chulo", "https://preview.redd.it/i-got-bored-so-i-decided-to-draw-a-random-image-on-the-v0-4ig97vv85vjb1.png?width=640&crop=smart&auto=webp&s=22ed6cc79cba3013b84967f32726d087e539b699");
        productService.createProduct("Electronica", "Producto2", "eBay", 30.0, "Muy útil", "String image");
        productService.createProduct("Electronica", "Producto3", "Walmart", 40.0, "Muy práctico", "String image");
        productService.createProduct("Deporte", "Producto4", "Decathlon", 50.0, "Muy resistente", "String image");
        productService.createProduct("Musica", "Producto5", "Zara", 60.0, "Muy elegante", "String image");


        userService.createUser("user1", "", "");

        reviewService.createReview("user1", "Muy bueno", 5);

 
    }
}