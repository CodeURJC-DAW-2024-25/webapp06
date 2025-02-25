package es.codeurjc.global_mart.config;

import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import es.codeurjc.global_mart.service.ReviewService;
import jakarta.annotation.PostConstruct;

import java.util.Arrays;

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

                productService.createProduct("Libros", "Producto1", "Amazon", 20.0, "Muy chulo", "String image");
                productService.createProduct("Electronica", "Producto2", "eBay", 30.0, "Muy útil",
                                "https://preview.redd.it/i-got-bored-so-i-decided-to-draw-a-random-image-on-the-v0-4ig97vv85vjb1.png?width=640&crop=smart&auto=webp&s=22ed6cc79cba3013b84967f32726d087e539b699");
                productService.createProduct("Electronica", "Producto3", "Walmart", 40.0, "Muy práctico",
                                "String image");
                productService.createProduct("Deporte", "Producto4", "Decathlon", 50.0, "Muy resistente",
                                "String image");
                productService.createProduct("Musica", "Producto5", "Zara", 60.0, "Muy elegante", "String image");
                productService.createProduct("Deporte", "Producto6", "H&M", 70.0, "Muy cómodo", "String image");
                productService.createProduct("Musica", "Producto7", "El Corte Inglés", 80.0, "Muy bonito",
                                "String image");
                productService.createProduct("Libros", "Producto8", "Carrefour", 90.0, "Muy interesante",
                                "String image");
                productService.createProduct("Electronica", "Producto9", "MediaMarkt", 100.0, "Muy moderno",
                                "String image");
                productService.createProduct("Hogar", "Producto10", "IKEA", 110.0, "Muy funcional", "String image");
                productService.createProduct("Electrodomesticos", "Producto11", "Best Buy", 120.0, "Muy eficiente",
                                "String image");
                productService.createProduct("Educacion", "Producto12", "Barnes & Noble", 130.0, "Muy educativo",
                                "String image");
                productService.createProduct("Cine", "Producto13", "Fandango", 140.0, "Muy entretenido",
                                "String image");
                productService.createProduct("Electronica", "Producto14", "AliExpress", 150.0, "Muy innovador",
                                "String image");
                productService.createProduct("Electronica", "Producto15", "Newegg", 160.0, "Muy avanzado",
                                "String image");

                userService.createUser(
                                "https://static.wikia.nocookie.net/disney/images/3/38/Mike1.png/revision/latest?cb=20230314012719&path-prefix=es",
                                "User 1", "user1", "user1@gmail.com", "user1", Arrays.asList("ROLE_user"));

                reviewService.createReview("user1", "Muy bueno", 5);

        }
}