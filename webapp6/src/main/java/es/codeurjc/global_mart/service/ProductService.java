package es.codeurjc.global_mart.service;

import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import es.codeurjc.global_mart.dto.Product.CompanyStadsDTO;
import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.Product.ProductMapper;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.Review;
import es.codeurjc.global_mart.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired ProductMapper productMapper;


    public ProductDTO createProduct(String type, String name, String business, Double price, String description,
            Blob image, Integer stock, Boolean isAccepted) throws IOException {

        Product product = new Product(type, name, business, price, description, stock, isAccepted);

        if (image != null) {
            product.setImage(image); // set image from blob
        } else {
            product.setImage(BlobProxy.generateProxy(
                    "https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png"
                            .getBytes()));
        }

        productRepository.save(product);
        return productMapper.toProductDTO(product);
    }

    public ProductDTO createProduct(String type, String name, String business, Double price, String description,
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

        productRepository.save(product);
        return productMapper.toProductDTO(product);
    }

    public ProductDTO addProduct(Product product) {
        productRepository.save(product);
        return productMapper.toProductDTO(product);
    }

    public ProductDTO updateProduct(Long id, Product product) {
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
            productRepository.save(updatedProduct);
            return productMapper.toProductDTO(updatedProduct);
        } else {
            throw new RuntimeException("Product not found with id " + id);
        }
    }

    public List<ProductDTO> getAllProducts() {
        return productMapper.toProductsDTO(productRepository.findAll());
    }

    public List<ProductDTO> getProductsByType(String type) {
        return productMapper.toProductsDTO(productRepository.findByType(type));
    }

    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id).map(productMapper::toProductDTO);
    }

    public ProductDTO updateProduct(Long id, String name, Double price) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(name);
            product.setPrice(price);
            product.setIsAccepted(true);
            productRepository.save(product);
            return productMapper.toProductDTO(product);
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

    public List<ProductDTO> getAcceptedProductsByType(String type) {
        return productMapper.toProductsDTO(productRepository.findByIsAcceptedTrueAndType(type));
    }

    public Page<ProductDTO> getAcceptedProductsByType(String type, Pageable pageable) {
        Page<Product> productsPage = productRepository.findByIsAcceptedTrueAndType(type, pageable);
        return productsPage.map(productMapper::toProductDTO);
    }

    public List<ProductDTO> getAcceptedProducts() {
        return productMapper.toProductsDTO(productRepository.findByIsAcceptedTrue());
    }

    public Page<ProductDTO> getAcceptedProducts(Pageable pageable) {
        Page<Product> productsPage = productRepository.findByIsAcceptedTrue(pageable);
        return productsPage.map(productMapper::toProductDTO);
    }

    public List<ProductDTO> getNotAcceptedProducts() {
        return productMapper.toProductsDTO(productRepository.findByIsAcceptedFalse());

    }

    public List<ProductDTO> searchProductsByName(String query) {
        return productMapper.toProductsDTO(productRepository.findByNameContainingIgnoreCaseAndIsAcceptedTrue(query));
    }

    public List<ProductDTO> searchProductsByNameAndType(String query, String type) {
        return productMapper.toProductsDTO(productRepository.findByNameContainingIgnoreCaseAndTypeAndIsAcceptedTrue(query, type));
    }

    public List<ProductDTO> getAcceptedCompanyProducts(String company) {
        List<Product> allProducts = productRepository.findAll();
        List<Product> acceptedCompanyProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getIsAccepted() && product.getCompany().equals(company)) {
                acceptedCompanyProducts.add(product);
            }
        }
        return productMapper.toProductsDTO(acceptedCompanyProducts);

    }

    public Page<ProductDTO> getAcceptedCompanyProducts(String company, Pageable pageable) {
        Page<Product> productsPage = productRepository.findByIsAcceptedTrueAndCompany(company, pageable);
        return productsPage.map(productMapper::toProductDTO);
    }

    public List<CompanyStadsDTO> getCompanyStadistics(String company){

        Map<String, Integer> dataMap = new HashMap<>();

        // Initialize the dataMap with predefined keys and zero values
        dataMap.put("Technology", 0);
        dataMap.put("Books", 0);
        dataMap.put("Education", 0);
        dataMap.put("Sports", 0);
        dataMap.put("Home", 0);
        dataMap.put("Music", 0);
        dataMap.put("Cinema", 0);
        dataMap.put("Appliances", 0);
        dataMap.put("Others", 0);


        List<Product> companyProducts = productMapper.toProducts(productService.getAcceptedCompanyProducts(company));
        
        for (Product product : companyProducts) {
            String type = product.getType();
            dataMap.put(type, dataMap.getOrDefault(type, 0) + 1);
        }

        List<CompanyStadsDTO> dataList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            dataList.add(new CompanyStadsDTO(entry.getKey(),entry.getValue()));
        }

        return dataList;

    }

    public List<ProductDTO> getMostViewedProducts(int limit) {
        List<Product> acceptedProducts = productRepository.findByIsAcceptedTrue();
        
        // order products by visit number (high to low)
        Collections.sort(acceptedProducts, (p1, p2) -> p2.getViews_count().compareTo(p1.getViews_count()));

        // take only the limit number products
        int size = Math.min(limit, acceptedProducts.size());
        return productMapper.toProductsDTO(acceptedProducts.subList(0, size));
    }

    public List<ProductDTO> getLastProducts(int limit) {
        List<Product> acceptedProducts = productRepository.findByIsAcceptedTrue();
        
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
        return productMapper.toProductsDTO(acceptedProducts.subList(0, size));
    }

    public Page<ProductDTO> getProductsPage(Pageable pageable) {
        Page<Product> productsPage = productRepository.findAll(pageable);
        return productsPage.map(productMapper::toProductDTO);
    }
}