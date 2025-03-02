package es.codeurjc.global_mart.config;

import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import es.codeurjc.global_mart.service.ReviewService;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.hibernate.engine.jdbc.BlobProxy;
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

                userService.createUser(null, "User 1", "user1", "user1@gmail.com", passwordEncoder.encode("user1"),
                                Arrays.asList("ADMIN"));

                userService.createUser(null, "comp", "comp", "user1@gmail.com", passwordEncoder.encode("comp"),
                                Arrays.asList("COMPANY"));

                userService.createUser(null, "a", "a", "a@gmail.com", passwordEncoder.encode("a"),
                                Arrays.asList("USER"));

                reviewService.createReview("user1", "Muy bueno", 5);

        }

}