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
        productService.createProduct("Product 1", 10.0);
        productService.createProduct("Product 2", 20.0);
        productService.createProduct("Product 3", 30.0);
        productService.createProduct("Product 4", 40.0);
        productService.createProduct("Product 5", 50.0);
    }
}