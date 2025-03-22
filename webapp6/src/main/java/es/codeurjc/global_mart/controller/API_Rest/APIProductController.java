package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;
import es.codeurjc.global_mart.dto.Product.ProductMapper;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class APIProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<ProductDTO> products = productService.getAcceptedProducts();
        List<Product> productsThrow = productMapper.toProducts(products);
        return ResponseEntity.ok(productsThrow);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<ProductDTO> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(productMapper.toProduct(product.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product productPassed) {
        ProductDTO product = productMapper.toProductDTO(productPassed);
        ProductDTO savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(productMapper.toProduct(savedProduct));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        ProductDTO updatedProduct = productService.updateProduct(id, product);
        if (updatedProduct != null) {
            return ResponseEntity.ok(productMapper.toProduct(updatedProduct));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.getProductById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
