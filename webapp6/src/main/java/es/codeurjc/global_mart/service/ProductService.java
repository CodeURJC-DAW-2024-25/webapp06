package es.codeurjc.global_mart.service;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            product.setImage(image); // como se sube una
                                     // imagen con blob a
                                     // partir de
                                     // multipartfile
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
            product.setImage(image); // como se sube una
                                    // imagen con blob a
                                    // partir de
                                    // multipartfile
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

    public String getProductName(Product product) {
        return product.getName();
    }

    public String getProductType(Product product) {
        return product.getType();
    }

    public String getProductCompany(Product product) {
        return product.getCompany();
    }

    public Double getProductPrice(Product product) {
        return product.getPrice();
    }

    public String getProductDescription(Product product) {
        return product.getDescription();
    }

    public Blob getProductImage(Product product) {
        return product.getImage();
    }

    public Long getProductId(Product product) {
        return product.getId();
    }

    public Integer getViews_product_count(Product product) {
        return product.getViews_count();
    }

    public void setViews_product_count(Product product) {
        product.setViews_count(getViews_product_count(product) + 1);
        productRepository.save(product);
    }

    public List<Product> getAcceptedProductsByType(String type) {
        List<Product> filterType = productRepository.findByType(type);
        List<Product> acceptedProducts = new ArrayList<>();
        for (Product product : filterType) {
            if (product.getIsAccepted()) {
                acceptedProducts.add(product);
            }
        }
        return acceptedProducts;
    }

    public List<Product> getAcceptedProducts() {
        List<Product> allProducts = productRepository.findAll();
        List<Product> acceptedProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getIsAccepted()) {
                acceptedProducts.add(product);
            }
        }
        return acceptedProducts;
    }

    public List<Product> getNotAcceptedProducts() {
        List<Product> allProducts = productRepository.findAll();
        List<Product> notacceptedProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (!product.getIsAccepted()) {
                notacceptedProducts.add(product);
            }
        }
        return notacceptedProducts;
    }

    public List<Product> searchProductsByName(String query) {
        System.out.println("Buscando productos con nombre que contenga: '" + query + "'");
        List<Product> allProducts = productRepository.findAll();
        List<Product> matchedProducts = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getName() != null &&
                    product.getName().toLowerCase().contains(query.toLowerCase())) {
                System.out.println("Coincidencia encontrada: " + product.getName());
                matchedProducts.add(product);
            }
        }

        System.out.println("Total de coincidencias: " + matchedProducts.size());
        return matchedProducts;
    }

    public List<Product> searchProductsByNameAndType(String query, String type) {
        return productRepository.findByNameContainingIgnoreCaseAndType(query, type);
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

    public List<Product> getMostViewedProducts(int limit) {
        List<Product> acceptedProducts = getAcceptedProducts();

        // Ordenar productos por número de vistas (de mayor a menor)
        Collections.sort(acceptedProducts, (p1, p2) -> p2.getViews_count().compareTo(p1.getViews_count()));

        // Tomar solo los primeros 'limit' productos
        int size = Math.min(limit, acceptedProducts.size());
        return acceptedProducts.subList(0, size);
    }
}