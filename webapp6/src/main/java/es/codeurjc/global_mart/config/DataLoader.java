package es.codeurjc.global_mart.config;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import es.codeurjc.global_mart.model.Review;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import jakarta.annotation.PostConstruct;

@Controller
public class DataLoader {

        @Autowired
        private ProductService productService;

        @Autowired
        private UserService userService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @PostConstruct
        public void loadData() throws IOException {

                byte[] image1 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/diariogreg.jpg").getFile().toPath());
                byte[] image2 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/iphone16.jpg").getFile().toPath());
                byte[] image3 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/macbook.jpg").getFile().toPath());
                byte[] image4 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/at10.jpg").getFile().toPath());
                byte[] image5 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/disco.jpg").getFile().toPath());
                byte[] image6 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/lavadora_samsung.jpg").getFile()
                                                .toPath());
                byte[] image7 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/tv_55.jpg").getFile().toPath());
                byte[] image8 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/conjunto-mesa-sillas.jpg").getFile()
                                                .toPath());
                byte[] image9 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/cortacesped.jpeg").getFile().toPath());
                byte[] image10 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/zapatillas.jpg").getFile().toPath());
                byte[] image11 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/codigo_davinci.jpg").getFile().toPath());
                byte[] image12 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/tablet_galaxy.jpg").getFile().toPath());
                byte[] image13 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/smartwatch.jpg").getFile().toPath());
                byte[] image14 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/cafetera.jpg").getFile().toPath());
                byte[] image15 = Files.readAllBytes(
                                new ClassPathResource("static/images/products/mancuernas.jpg").getFile().toPath());

                // Create and associate reviews before persisting
                Review review1 = new Review("user1", "Muy bueno", 5);
                Review review2 = new Review("user1", "Muy malo", 1);

                // Create product
                productService.createProduct("Books", "Libro El Quijote", "LaCasaDelLibro", 20.0,
                                "Una versión abreviada de las aventuras de un excéntrico caballero rural y su fiel compañero...",
                                BlobProxy.generateProxy(image6), 10, true, List.of(review1, review2));

                productService.createProduct("Books", "Producto1", "Amazon", 20.0, "Muy chulo",
                                BlobProxy.generateProxy(image1), 10, true);
                productService.createProduct("Technology", "Producto2", "eBay", 30.0, "Muy útil",
                                BlobProxy.generateProxy(image2), 10, true);
                productService.createProduct("Technology", "Producto3", "comp", 40.0, "Muy práctico", BlobProxy
                                .generateProxy(image3), 10,
                                true);
                productService.createProduct("Sports", "Producto4", "Decathlon", 50.0, "Muy resistente",
                                BlobProxy.generateProxy(image4), 10,
                                true);
                productService.createProduct("Music", "Producto5", "Zara", 60.0, "Muy elegante",
                                BlobProxy.generateProxy(image5), 10, true);
                productService.createProduct("Appliances", "Lavadora Samsung", "ElectroMax", 399.99,
                                "Lavadora de carga frontal con tecnología EcoBubble",
                                BlobProxy.generateProxy(image6), 5, true);
                productService.createProduct("Technology", "Televisor LED 55\"", "MediaMarkt", 549.99,
                                "Televisor UHD 4K con Smart TV integrado",
                                BlobProxy.generateProxy(image7), 8, true);
                productService.createProduct("Appliances", "Conjunto mesa y sillas", "IKEA", 199.99,
                                "Set de comedor moderno con 4 sillas",
                                BlobProxy.generateProxy(image8), 3, true);
                productService.createProduct("Appliances", "Cortacésped eléctrico", "Jardiland", 149.95,
                                "Potente cortacésped con batería recargable",
                                BlobProxy.generateProxy(image9), 7, true);
                productService.createProduct("Sports", "Zapatillas deportivas", "Nike", 89.99,
                                "Zapatillas ligeras para running",
                                BlobProxy.generateProxy(image10), 15, true);
                productService.createProduct("Books", "El Código Da Vinci", "Casa del Libro", 15.50,
                                "Bestseller mundial de Dan Brown",
                                BlobProxy.generateProxy(image11), 20, true);
                productService.createProduct("Technology", "Tablet Galaxy", "Samsung", 299.99,
                                "Tablet con pantalla AMOLED de 10.5\"",
                                BlobProxy.generateProxy(image12), 6, true);
                productService.createProduct("Technology", "Smartwatch Fitness", "GadgetWorld", 129.99,
                                "Reloj inteligente con monitorización de actividad física y notificaciones",
                                BlobProxy.generateProxy(image13), 12, true);
                productService.createProduct("Appliances", "Cafetera Automática", "HomeAppliances", 89.95,
                                "Cafetera programable con molinillo de granos integrado",
                                BlobProxy.generateProxy(image14), 8, true);
                productService.createProduct("Sports", "Mancuernas Ajustables", "FitnessDepot", 199.95,
                                "Juego de mancuernas con pesos intercambiables de 2 a 20 kg",
                                BlobProxy.generateProxy(image15), 4, true);

                userService.createUser(null, "User 1", "user1", "user1@gmail.com", passwordEncoder.encode("user1"),
                                Arrays.asList("ADMIN"));

                userService.createUser(null, "comp", "comp", "user1@gmail.com", passwordEncoder.encode("comp"),
                                Arrays.asList("COMPANY"));

                userService.createUser(null, "a", "a", "a@gmail.com", passwordEncoder.encode("a"),
                                Arrays.asList("USER"));

        }

}