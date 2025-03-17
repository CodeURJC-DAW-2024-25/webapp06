package es.codeurjc.global_mart.service;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.Review;
import es.codeurjc.global_mart.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(String type, String name, String business, Double price, String description,
            Blob image, Integer stock, Boolean isAccepted) throws IOException {

        Product product = new Product(type, name, business, price, description, stock, isAccepted);

        if (image != null) {
            product.setImage(image); // set image from blob
        } else {
            product.setImage(BlobProxy.generateProxy(
                    "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png"
                            .getBytes()));
        }

        return productRepository.save(product);
    }

    public Product createProduct(String type, String name, String business, Double price, String description,
            Blob image, Integer stock, Boolean isAccepted, List<Review> reviews) throws IOException {
        Product product = new Product(type, name, business, price, description, stock, isAccepted);
        product.setReviews(reviews);

        logger.info("Number of reviews: " + reviews.size());

        if (image != null) {
            product.setImage(image); // set image from blob
        } else {
            product.setImage(BlobProxy.generateProxy(
                    "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png"
                            .getBytes()));
        }

        return productRepository.save(product);
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product product) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product updatedProduct = optionalProduct.get();
            updatedProduct.setName(product.getName());
            updatedProduct.setPrice(product.getPrice());
            updatedProduct.setStock(product.getStock());
            updatedProduct.setDescription(product.getDescription());
            updatedProduct.setType(product.getType());
            updatedProduct.setCompany(product.getCompany());
            updatedProduct.setIsAccepted(product.getIsAccepted());
            updatedProduct.setReviews(product.getReviews());
            updatedProduct.setImage(product.getImage());
            return productRepository.save(updatedProduct);
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
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
            product.setIsAccepted(true);
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void setViews_product_count(Product product) {
        product.setViews_count(product.getViews_count() + 1);
        productRepository.save(product);
    }

    public List<Product> getAcceptedProductsByType(String type) {
        return productRepository.findByIsAcceptedTrueAndType(type);
    }

    public Page<Product> getAcceptedProductsByType(String type, Pageable pageable) {
        return productRepository.findByIsAcceptedTrueAndType(type, pageable);
    }

    public List<Product> getAcceptedProducts() {
        return productRepository.findByIsAcceptedTrue();
    }

    public Page<Product> getAcceptedProducts(Pageable pageable) {
        return productRepository.findByIsAcceptedTrue(pageable);
    }

    public List<Product> getNotAcceptedProducts() {
        return productRepository.findByIsAcceptedFalse();

    }

    /*
     * public List<Product> searchProductsByName(String query) {
     * System.out.println("Buscando productos con nombre que contenga: '" + query +
     * "'");
     * List<Product> allProducts = productRepository.findAll();
     * List<Product> matchedProducts = new ArrayList<>();
     * 
     * for (Product product : allProducts) {
     * if (product.getName() != null &&
     * product.getName().toLowerCase().contains(query.toLowerCase())) {
     * System.out.println("Coincidencia encontrada: " + product.getName());
     * matchedProducts.add(product);
     * }
     * }
     * 
     * System.out.println("Total de coincidencias: " + matchedProducts.size());
     * return matchedProducts;
     * }
     */
    public List<Product> searchProductsByName(String query) {
        return productRepository.findByNameContainingIgnoreCaseAndIsAcceptedTrue(query);
    }

    public List<Product> searchProductsByNameAndType(String query, String type) {
        return productRepository.findByNameContainingIgnoreCaseAndTypeAndIsAcceptedTrue(query, type);
    }

    public List<Product> getAcceptedCompanyProducts(String company) {
        List<Product> allProducts = productRepository.findAll();
        List<Product> acceptedCompanyProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getIsAccepted() && product.getCompany().equals(company)) {
                acceptedCompanyProducts.add(product);
            }
        }
        return acceptedCompanyProducts;

    }

    public Page<Product> getAcceptedCompanyProducts(String company, Pageable pageable) {
        return productRepository.findByIsAcceptedTrueAndCompany(company, pageable);
    }

    public List<Product> getMostViewedProducts(int limit) {
        List<Product> acceptedProducts = getAcceptedProducts();

        // order products by visit number (high to low)
        Collections.sort(acceptedProducts, (p1, p2) -> p2.getViews_count().compareTo(p1.getViews_count()));

        // take only the limit number products
        int size = Math.min(limit, acceptedProducts.size());
        return acceptedProducts.subList(0, size);
    }

    public List<Product> getLastProducts(int limit) {
        List<Product> acceptedProducts = getAcceptedProducts();

        // sort accepted products by creation date
        Collections.sort(acceptedProducts, (p1, p2) -> p2.getDate().compareTo(p1.getDate()));
        int size = Math.min(limit, acceptedProducts.size());
        System.out.println("lista de aceptados");
        for (Product product : acceptedProducts) {
            System.out.println("Fecha:" + product.getDate() + ", Nombre: " + product.getName());
        }
        System.out.println("sublista");
        for (Product product : acceptedProducts.subList(0, size)) {
            System.out.println("Fecha: " + product.getDate() + ", Nombre: " + product.getName());
        }
        return acceptedProducts.subList(0, size);
    }

    public Page<Product> getProductsPage(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}