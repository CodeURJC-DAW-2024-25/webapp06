package es.codeurjc.global_mart.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.data.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.global_mart.dto.Product.CompanyStadsDTO;
import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.Product.ProductMapper;
import es.codeurjc.global_mart.dto.Product.SearchProductDTO;
import es.codeurjc.global_mart.dto.Reviewss.ReviewDTO;
import es.codeurjc.global_mart.dto.Reviewss.ReviewMapper;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.Review;
import es.codeurjc.global_mart.repository.ProductRepository;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    // @Autowired
    // private ProductService productService;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ReviewMapper reviewMapper;

    public ProductDTO createProduct(String type, String name, String business, Double price, String description,
            Blob image, Integer stock, Boolean isAccepted) throws IOException {

        Product product = new Product(type, name, business, price, description, stock, isAccepted);

        productRepository.save(product);
        return productMapper.toProductDTO(product);
    }


    public ProductDTO addProduct(ProductDTO product) {
        Product product2 = productMapper.toProduct(product);
        productRepository.save(product2);
        return productMapper.toProductDTO(product2);
    }

    public void addReviewToProduct(ProductDTO productDTO, ReviewDTO reviewDTO) {
        Product product = productMapper.toProduct(productDTO);
        Review review = reviewMapper.toReview(reviewDTO);
        product.addReview(review);
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

    public List<SearchProductDTO> getAllProductsToSearch() {
        return productMapper.toSearchProductsDTO(productRepository.findAll());
    }

    public List<ProductDTO> getProductsByType(String type) {
        return productMapper.toProductsDTO(productRepository.findByType(type));
    }

    public List<SearchProductDTO> getProductsByTypeToSearch(String type) {
        return productMapper.toSearchProductsDTO(productRepository.findByType(type));
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

    public ProductDTO deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow();

        // As books are related to shops, it is needed to load the book shops
        // before deleting it to avoid LazyInitializationException
        ProductDTO productDTO = productMapper.toProductDTO(product);
        productRepository.deleteById(id);

        return productDTO;
    }

    public void setViews_product_count(ProductDTO productDTO) {
        Product product = productMapper.toProduct(productDTO);
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

    public List<SearchProductDTO> searchProductsByName(String query) {
        return productMapper
                .toSearchProductsDTO(productRepository.findByNameContainingIgnoreCaseAndIsAcceptedTrue(query));
    }

    public List<SearchProductDTO> searchProductsByNameAndType(String query, String type) {
        return productMapper
                .toSearchProductsDTO(
                        productRepository.findByNameContainingIgnoreCaseAndTypeAndIsAcceptedTrue(query, type));
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

    public List<CompanyStadsDTO> getCompanyStadistics(String company) {

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

        List<Product> companyProducts = productMapper.toProducts(getAcceptedCompanyProducts(company));

        for (Product product : companyProducts) {
            String type = product.getType();
            dataMap.put(type, dataMap.getOrDefault(type, 0) + 1);
        }

        List<CompanyStadsDTO> dataList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            dataList.add(new CompanyStadsDTO(entry.getKey(), entry.getValue()));
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

    public List<ProductDTO> getLastProducts() {
        int limit = 4;
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

    public void convertBlobToBase64(List<ProductDTO> products) {
        List<Product> productsList = productMapper.toProducts(products);
        for (Product product : productsList) {
            try {
                Blob imageBlob = product.getImage();
                if (imageBlob != null) {
                    byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
                    product.setImageBase64(imageBase64);
                } else {
                    // Default image
                    product.setImageBase64("/images/default-product.jpg");
                }
            } catch (Exception e) {
                e.printStackTrace();
                product.setImageBase64("/images/default-product.jpg");
            }
        }
    }

    public void convertBlobToBase64ToSearch(List<SearchProductDTO> products) {
        List<Product> productsList = productMapper.fromSearchToProducts(products);
        for (Product product : productsList) {
            try {
                Blob imageBlob = product.getImage();
                if (imageBlob != null) {
                    byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
                    product.setImageBase64(imageBase64);
                } else {
                    // Default image
                    product.setImageBase64("/images/default-product.jpg");
                }
            } catch (Exception e) {
                e.printStackTrace();
                product.setImageBase64("/images/default-product.jpg");
            }
        }
    }

    public List<ProductDTO> addImageDataToProducts(List<ProductDTO> products) {
        List<ProductDTO> productsList = new ArrayList<>();

        for (ProductDTO productDTO : products) {
            try {
                Product product = productMapper.toProduct(productDTO);
                Blob imageBlob = product.getImage();
                if (imageBlob != null) {
                    byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);

                    // necesitamos crear un nuevo DTO con la imagen en base64 para actualizarlo, ya
                    // que asignando otra vez lo unico que hacemos es actualizar la variable local
                    // de este codigo
                    ProductDTO updatedProduct = new ProductDTO(
                            productDTO.id(),
                            productDTO.type(),
                            productDTO.name(),
                            productDTO.company(),
                            productDTO.price(),
                            productDTO.description(),
                            productDTO.image(),
                            productDTO.stock(),
                            productDTO.isAccepted(),
                            productDTO.date(),
                            productDTO.views_count(),
                            productDTO.reviews(),
                            imageBase64 // Set the imageBase64 field
                    );

                    productsList.add(updatedProduct);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return productsList;
    }

    public ProductDTO addImageToASingleProduct(ProductDTO productDTO) {
        try {
            Product product = productMapper.toProduct(productDTO);
            Blob imageBlob = product.getImage();
            if (imageBlob != null) {
                byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
                String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);

                // necesitamos crear un nuevo DTO con la imagen en base64 para actualizarlo, ya
                // que asignando otra vez lo unico que hacemos es actualizar la variable local
                // de este codigo
                ProductDTO updatedProduct = new ProductDTO(
                        productDTO.id(),
                        productDTO.type(),
                        productDTO.name(),
                        productDTO.company(),
                        productDTO.price(),
                        productDTO.description(),
                        productDTO.image(),
                        productDTO.stock(),
                        productDTO.isAccepted(),
                        productDTO.date(),
                        productDTO.views_count(),
                        productDTO.reviews(),
                        imageBase64 // Set the imageBase64 field
                );

                return updatedProduct;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return productDTO;
    }

    public void updateProductDetails(ProductDTO productDTO, String name, String description, String type, Integer stock,
            Double price, MultipartFile image) {

        Product product = productMapper.toProduct(productDTO);
        product.setName(name);
        product.setDescription(description);
        product.setType(type);
        product.setStock(stock);
        product.setPrice(price);

        // update the image if it is not null
        if (image != null && !image.isEmpty()) {
            try {
                product.setImage(BlobProxy.generateProxy(
                        image.getInputStream(),
                        image.getSize()));
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        }

        addProduct(productMapper.toProductDTO(product));
    }


    public Resource getProductImage(long id) throws SQLException {

		Product product = productRepository.findById(id).orElseThrow();

		if (product.getImage() != null) {
			return new InputStreamResource(product.getImage().getBinaryStream());
		} else {
			throw new NoSuchElementException();
		}
	}

	public void createProductImage(long id, InputStream inputStream, long size) {

		Product product = productRepository.findById(id).orElseThrow();

		
		product.setImage(BlobProxy.generateProxy(inputStream, size));

		productRepository.save(product);
	}

	public void replaceProductImage(long id, InputStream inputStream, long size) {

		Product product = productRepository.findById(id).orElseThrow();

		if (product.getImage()==null) {
			throw new NoSuchElementException();
		}

		product.setImage(BlobProxy.generateProxy(inputStream, size));

		productRepository.save(product);
	}

	public void deleteProductImage(long id) {

		Product product = productRepository.findById(id).orElseThrow();

		if (product.getImage()==null) {
			throw new NoSuchElementException();
		}

		product.setImage(null);
		

		productRepository.save(product);
	}

}