package es.codeurjc.global_mart.service;

import es.codeurjc.global_mart.repository.ProductRepository;
import es.codeurjc.global_mart.model.Product;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(String type, String name, String business, Double price, String description, String image) {
        Product product = new Product(type, name, business, price, description, image);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    public List<Product> getProductsByType(String type) {
        return productRepository.findByType(type);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, String name, Double price) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(name);
            product.setPrice(price);
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }

 
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}