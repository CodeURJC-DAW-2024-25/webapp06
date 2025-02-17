package es.codeurjc.global_mart.config;

import es.codeurjc.global_mart.service.ProductService;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DataLoader {

    @Autowired
    private ProductService productService;
    
    @PostConstruct
    public void loadData() {
        productService.createProduct("Hogar", "Producto1", "Amazon", 20.0, "Muy chulo", "String image");
        productService.createProduct("Electrónica", "Producto2", "eBay", 30.0, "Muy útil", "String image");
        productService.createProduct("Jardinería", "Producto3", "Walmart", 40.0, "Muy práctico", "String image");
        productService.createProduct("Deportes", "Producto4", "Decathlon", 50.0, "Muy resistente", "String image");
        productService.createProduct("Moda", "Producto5", "Zara", 60.0, "Muy elegante", "String image");
    }
}