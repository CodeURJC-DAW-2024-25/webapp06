package es.codeurjc.global_mart.config;

import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import es.codeurjc.global_mart.service.ReviewService;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.Review;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.hibernate.engine.jdbc.BlobProxy;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataLoader {

        @Autowired
        private ProductService productService;

        @Autowired
        private UserService userService;

        @Autowired
        private ReviewService reviewService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @PostConstruct
        public void loadData() throws IOException {

                String imageDir = "src/main/resources/static/images/products/";

                byte[] image1 = Files.readAllBytes(Paths.get(imageDir + "diariogreg.jpg"));
                byte[] image2 = Files.readAllBytes(Paths.get(imageDir + "iphone16.jpg"));
                byte[] image3 = Files.readAllBytes(Paths.get(imageDir + "macbook.jpg"));
                byte[] image4 = Files.readAllBytes(Paths.get(imageDir + "at10.jpg"));
                byte[] image5 = Files.readAllBytes(Paths.get(imageDir + "disco.jpg"));

                productService.createProduct("Books", "Producto1", "Amazon", 20.0, "Muy chulo",
                                BlobProxy.generateProxy(image1), 10, true);
                productService.createProduct("Technology", "Producto2", "eBay", 30.0, "Muy útil",
                                BlobProxy.generateProxy(image2), 10, true);
                productService.createProduct("Technology", "Producto3", "Walmart", 40.0, "Muy práctico", BlobProxy
                                .generateProxy(image3), 10,
                                true);
                productService.createProduct("Sports", "Producto4", "Decathlon", 50.0, "Muy resistente",
                                BlobProxy.generateProxy(image4), 10,
                                true);
                productService.createProduct("Music", "Producto5", "Zara", 60.0, "Muy elegante",
                                BlobProxy.generateProxy(image5), 10, true);
                Review review1 = new Review("user1", "Muy bueno", 5, LocalDateTime.now());
                Review review2 = new Review("user1", "Muy malo", 1, LocalDateTime.now());

                reviewService.createReview("user1", "Muy bueno", 5);
                reviewService.createReview("user1", "Muy malo", 1);

                Product elQuijote = new Product("Books", "Libro El Quijote", "LaCasaDelLibro", 20.0, "Una versión abreviada de las aventuras de un excéntrico caballero rural y su fiel compañero, quienes partieron como caballero y escudero de antaño para corregir los errores y castigar el mal.",
                                         10,true);
                elQuijote.setReviews(List.of(review1, review2));
                productService.addProduct(elQuijote);
                
                productService.createProduct("Technology", "Producto2", "eBay", 30.0, "Muy útil", null, 10,true);
                productService.createProduct("Technology", "Producto3", "Walmart", 40.0, "Muy práctico", null, 10,true);
                productService.createProduct("Sports", "Producto4", "Decathlon", 50.0, "Muy resistente", null, 10,true);
                productService.createProduct("Music", "Producto5", "Zara", 60.0, "Muy elegante", null, 10,true);
                productService.createProduct("Sports", "Producto6", "H&M", 70.0, "Muy cómodo", null, 10,true);
                productService.createProduct("Music", "Producto7", "El Corte Inglés", 80.0, "Muy bonito", null, 10,true);
                productService.createProduct("Books", "Producto8", "Carrefour", 90.0, "Muy interesante", null, 10,true);
                productService.createProduct("Technology", "Producto9", "MediaMarkt", 100.0, "Muy moderno", null, 10,false);
                productService.createProduct("Home", "Producto10", "IKEA", 110.0, "Muy funcional", null, 10,true);
                productService.createProduct("Appliances", "Producto11", "Best Buy", 120.0, "Muy eficiente",
                                null, 10,true);
                productService.createProduct("Education", "Producto12", "Barnes & Noble", 130.0, "Muy educativo", null,
                                10,true);
                productService.createProduct("Cinema", "Producto13", "Fandango", 140.0, "Muy entretenido", null, 10,false);
                productService.createProduct("Technology", "Producto14", "AliExpress", 150.0, "Muy innovador", null,
                                10,true);
                productService.createProduct("Technology", "Producto15", "Newegg", 160.0, "Muy avanzado", null, 10,true);

                userService.createUser(null, "User 1", "user1", "user1@gmail.com", passwordEncoder.encode("user1"),
                                Arrays.asList("ADMIN"));

                userService.createUser(null, "comp", "comp", "user1@gmail.com", passwordEncoder.encode("comp"),
                                Arrays.asList("COMPANY"));

                userService.createUser(null, "a", "a", "a@gmail.com", passwordEncoder.encode("a"),
                                Arrays.asList("USER"));

                reviewService.createReview("user1", "Muy bueno", 5);
                
                userService.createUser(null, "a", "a", "a@gmail.com", passwordEncoder.encode("a"), Arrays.asList("USER"));

        }

}