package es.codeurjc.global_mart.service;

import es.codeurjc.global_mart.repository.ProductRepository;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.Review;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Blob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReviewService reviewService;

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

    public List<Review> getProductReviews(Product product) {
        return reviewService.getAllReviews();
    }

    public Long getProductId(Product product) {
        return product.getId();
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
}