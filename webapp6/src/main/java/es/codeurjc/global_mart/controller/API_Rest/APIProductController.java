package es.codeurjc.global_mart.controller.API_Rest;

import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.global_mart.dto.Product.ProductMapper;
import es.codeurjc.global_mart.dto.Reviewss.ReviewDTO;

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

	// ------------------------------------------BASIC
	// CRUD------------------------------------------

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
	public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO, Authentication authentication)
			throws IOException {

		if (authentication == null) {
			return ResponseEntity.status(401).body("Unauthorized");
		} else if (authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_COMPANY"))) {
			return ResponseEntity.status(403).body("Forbidden");
		}

		Object principal = authentication.getPrincipal();
		ProductDTO productDTOfinal = null;
		String username = null;

		if (principal instanceof OAuth2User oAuth2User) {
			username = oAuth2User.getAttribute("name");
		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			username = userDetails.getUsername();
		}

		if (username != null) {
			productDTOfinal = productService.addProduct(productDTO, username);
		}

		return ResponseEntity.ok(productDTOfinal);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable long id, @RequestBody ProductDTO productDTO,
			Authentication authentication)throws SQLException {

		if (authentication == null) return ResponseEntity.status(401).body("Unauthorized");
		
		Object principal = authentication.getPrincipal();
		String username = null;

		if (principal instanceof OAuth2User oAuth2User) {
			if (oAuth2User.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_COMPANY")) || 
				!productService.getProductById(id).get().company().equals(oAuth2User.getAttribute("name"))) {

				return ResponseEntity.status(403).body("Forbidden");
			}
			
			username = oAuth2User.getAttribute("name");

		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_COMPANY")) || 
			!productService.getProductById(id).get().company().equals(userDetails.getUsername())) {

				return ResponseEntity.status(403).body("Forbidden");
			}

			username = userDetails.getUsername();

		}

		if (username != null) {
			ProductDTO updatedBookDTO = productService.updateProduct(id, productDTO, username);
			return ResponseEntity.ok(updatedBookDTO);
		}else {
			return ResponseEntity.status(401).body("Unauthorized");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable long id, Authentication authentication) {

		if (authentication == null) return ResponseEntity.status(401).body("Unauthorized");
		
		Object principal = authentication.getPrincipal();

		if (principal instanceof OAuth2User oAuth2User) {
			if (oAuth2User.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_COMPANY")) || 
				!productService.getProductById(id).get().company().equals(oAuth2User.getAttribute("name"))) {

				return ResponseEntity.status(403).body("Forbidden");
			}

		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_COMPANY")) || 
			!productService.getProductById(id).get().company().equals(userDetails.getUsername())) {

				return ResponseEntity.status(403).body("Forbidden");
			}

		}

		return ResponseEntity.ok(productService.deleteProduct(id));
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

	// ------------------------------------------ALGORITHM------------------------------------------
}
