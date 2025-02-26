package es.codeurjc.global_mart.config;

import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import es.codeurjc.global_mart.service.ReviewService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
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
        public void loadData() throws IOException {

                productService.createProduct("Libros", "Producto1", "Amazon", 20.0, "Muy chulo", null, 10);
                productService.createProduct("Electronica", "Producto2", "eBay", 30.0, "Muy útil", null, 10);
                productService.createProduct("Electronica", "Producto3", "Walmart", 40.0, "Muy práctico", null, 10);
                productService.createProduct("Deporte", "Producto4", "Decathlon", 50.0, "Muy resistente", null, 10);
                productService.createProduct("Musica", "Producto5", "Zara", 60.0, "Muy elegante", null, 10);
                productService.createProduct("Deporte", "Producto6", "H&M", 70.0, "Muy cómodo", null, 10);
                productService.createProduct("Musica", "Producto7", "El Corte Inglés", 80.0, "Muy bonito", null, 10);
                productService.createProduct("Libros", "Producto8", "Carrefour", 90.0, "Muy interesante", null, 10);
                productService.createProduct("Electronica", "Producto9", "MediaMarkt", 100.0, "Muy moderno", null, 10);
                productService.createProduct("Hogar", "Producto10", "IKEA", 110.0, "Muy funcional", null, 10);
                productService.createProduct("Electrodomesticos", "Producto11", "Best Buy", 120.0, "Muy eficiente",
                                null, 10);
                productService.createProduct("Educacion", "Producto12", "Barnes & Noble", 130.0, "Muy educativo", null,
                                10);
                productService.createProduct("Cine", "Producto13", "Fandango", 140.0, "Muy entretenido", null, 10);
                productService.createProduct("Electronica", "Producto14", "AliExpress", 150.0, "Muy innovador", null,
                                10);
                productService.createProduct("Electronica", "Producto15", "Newegg", 160.0, "Muy avanzado", null, 10);

                userService.createUser(null, "User 1", "user1", "user1@gmail.com", "user1",
                                Arrays.asList("ROLE_COMPANY"));

                reviewService.createReview("user1", "Muy bueno", 5);

        }
}