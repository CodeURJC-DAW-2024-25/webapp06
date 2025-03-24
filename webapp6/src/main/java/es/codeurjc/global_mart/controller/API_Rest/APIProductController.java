package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.global_mart.dto.Product.ProductMapper;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class APIProductController {

	@Autowired
	private ProductService productService;

    @Autowired
    private ProductMapper productMapper;
    
    @GetMapping("/")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAcceptedProducts();
        return ResponseEntity.ok(products);
    }

	@GetMapping("/type/{type}")
	public ResponseEntity<List<ProductDTO>> getProductsByType(@PathVariable String type) {
		List<ProductDTO> products = productService.getProductsByType(type);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        Optional<ProductDTO> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

	@PostMapping("/")
	public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) throws IOException {

		productDTO = productService.createProduct(productDTO.type(), productDTO.name(), productDTO.company(),
				productDTO.price(), productDTO.description(), null, productDTO.stock(), productDTO.isAccepted(),
				productDTO.reviews());

		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(productDTO.id()).toUri();

		return ResponseEntity.created(location).body(productDTO);
	}

	@PutMapping("/{id}")
	public ProductDTO updateProduct(@PathVariable long id, @RequestBody ProductDTO updatedProductDTO)
			throws SQLException {
		Product updatedBookDTO = productMapper.toProduct(updatedProductDTO);
		return productService.updateProduct(id, updatedBookDTO);
	}

	@DeleteMapping("/{id}")
	public ProductDTO deleteProduct(@PathVariable long id) {

		return productService.deleteProduct(id);
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<Object> getProductImage(@PathVariable long id) throws SQLException, IOException {

		Resource postImage = productService.getProductImage(id);

		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
				.body(postImage);

	}

	@PostMapping("/{id}/image")
	public ResponseEntity<Object> createProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		productService.createProductImage(id, imageFile.getInputStream(), imageFile.getSize());

		URI location = fromCurrentRequest().build().toUri();

		return ResponseEntity.created(location).build();
	}

	@PutMapping("/{id}/image")
	public ResponseEntity<Object> replaceProductImage(@PathVariable long id, @RequestParam MultipartFile imageFile)
			throws IOException {

		productService.replaceProductImage(id, imageFile.getInputStream(), imageFile.getSize());

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}/image")
	public ResponseEntity<Object> deleteProductImage(@PathVariable long id) throws IOException {

		productService.deleteProductImage(id);

		return ResponseEntity.noContent().build();
	}
}
