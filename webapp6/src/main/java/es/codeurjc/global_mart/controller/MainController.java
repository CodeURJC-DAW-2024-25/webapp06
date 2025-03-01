package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Collections;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.User;

// import es.codeurjc.global_mart.model.LoggedUser;
// import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.sql.Blob;

@Controller
public class MainController {

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	// @Autowired
	// private LoggedUser loggedUser;

	// @Autowired
	// private UserRepository userRepository;

	private Principal principal;

	@ModelAttribute
	public void addAtributes(Model model, HttpServletRequest request) {

		principal = request.getUserPrincipal();

		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("username", principal.getName());

			Optional<User> user = userService.findByUsername(principal.getName());
			if (user.isPresent() && user.get().isAdmin()) {
				model.addAttribute("isAdmin", true);
				model.addAttribute("isCompany", false);
				model.addAttribute("isUser", false);
			} else if (user.isPresent() && user.get().isCompany()) {
				model.addAttribute("isAdmin", false);
				model.addAttribute("isCompany", true);
				model.addAttribute("isUser", false);
			} else {
				model.addAttribute("isAdmin", false);
				model.addAttribute("isCompany", false);
				model.addAttribute("isUser", true);
			}

		} else {
			model.addAttribute("logged", false);
		}
	}

	// Functions to redirect to the different pages of the application
	// Initial page (index.html)
	@GetMapping("/")
	public String greeting(Model model) {
		return "index";
	}

	// Redirection to the initial page
	@GetMapping("/redir")
	public String redir(Model model) {
		return "redirect:/";
	}

	// Redirection to the login page
	@GetMapping("/choose_login_option")
	public String choose_login(Model model) {
		return "choose_login_option";
	}

	@GetMapping("/login_error")
	public String login_error(Model model) {
		return "login_error";
	}

	// Redirection to the about us page
	@GetMapping("/about")
	public String about_us(Model model) {
		return "about";
	}

	@GetMapping("/adminPage")
	public String admin(Model model) {
		model.addAttribute("productsNotAccepted", productService.getNotAcceptedProducts());
		return "administrator";
	}

	// Redirection to the user page
	@GetMapping("/profile")
	public String profile(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		String username = principal.getName();

		// Assuming you have a UserService to fetch user details
		Optional<User> user = userService.findByUsername(username);

		if (user.isPresent()) {
			model.addAttribute("username", user.get().getUsername());
			model.addAttribute("email", user.get().getEmail());
			model.addAttribute("profile_image", user.get().getImage());
		}

		return "user";
	}

	@GetMapping("/products/allProducts")
	public String seeAllProds(Model model, HttpServletRequest request) {
		List<Product> products = productService.getAcceptedProducts();
		addImageDataToProducts(products);
		model.addAttribute("allProds", productService.getAcceptedProducts());
		model.addAttribute("tittle", false);

		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			model.addAttribute("allCompanyProds", Collections.emptyList());
		} else {
			Optional<User> user = userService.findByUsername(principal.getName());
			if (user.isPresent() && user.get().isCompany()) {
				model.addAttribute("allCompanyProds",
						productService.getAcceptedCompanyProducts(user.get().getUsername()));
			}
		}
		return "products";
	}

	// Redirection to see ONLY the products of a specific type
	@GetMapping("/products/{type}")
	public String getMethodName(@PathVariable String type, Model model) {

		List<Product> products = productService.getAcceptedProductsByType(type);
		addImageDataToProducts(products);
		model.addAttribute("allProds", products);
		model.addAttribute("type", type);
		model.addAttribute("tittle", true);
		return "products";
	}

	private void addImageDataToProducts(List<Product> products) {
		for (Product product : products) {
			try {
				Blob imageBlob = product.getImage();
				if (imageBlob != null) {
					byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
					String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
					product.setImageBase64(imageBase64); // Necesitas añadir este campo a la clase Product
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping("/product/{id}")
	public String productDescription(@PathVariable Long id, Model model) throws Exception {
		Optional<Product> product = productService.getProductById(id); // Extract the product by its id

		if (product.isPresent()) {
			// Extract all the info of the product to use it in the musctache template
			model.addAttribute("productName", productService.getProductName(product.get()));
			model.addAttribute("productType", productService.getProductType(product.get()));
			model.addAttribute("productCompany", productService.getProductCompany(product.get()));
			model.addAttribute("productPrice", productService.getProductPrice(product.get()));
			model.addAttribute("productDescription", productService.getProductDescription(product.get()));

			// Convert Blob to Base64 encoded string
			String imageBase64 = null;
			Blob imageBlob = product.get().getImage();
			if (imageBlob != null) {
				byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
				imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
			}
			model.addAttribute("productImage", imageBase64);

			model.addAttribute("productId", productService.getProductId(product.get()));
			model.addAttribute("productStock", product.get().getStock());
			model.addAttribute("reviews", productService.getProductReviews(product.get()));


			return "descriptionProduct";
		} else {
			return "redirect:/products/allProducts";
		}
	}

	// Redirection to the descriprion of a produdct
	@GetMapping("/descriptionProduct")
	public String descriptionProduct(Model model) {
		// model.addAttribute("token", ); // take token for the post method
		return "descriptionProduct";
	}

	@GetMapping("/new_product")
	public String new_product(Model model) {
		return "uploadProducts";
	}

	@PostMapping("/newproduct")
	public String newproduct(@RequestParam String product_name, @RequestParam MultipartFile product_image,
			@RequestParam String product_description, @RequestParam String product_type,
			@RequestParam Integer product_stock, @RequestParam Double product_price)
			throws Exception {

		// Usamos el parámetro Principal para obtener el nombre del usuario logueado
		productService.createProduct(product_type, product_name, principal.getName(),
				product_price,
				product_description, BlobProxy.generateProxy(product_image.getInputStream(), product_image.getSize()),
				product_stock, false);

		return "redirect:/products/allProducts";
	}

	@GetMapping("/acceptProduct/{id}")
	public String acceptProduct(@PathVariable Long id) {
		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent()) {
			productService.updateProduct(id, productService.getProductName(product.get()),
					productService.getProductPrice(product.get()));
		}
		return "redirect:/adminPage";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return "redirect:/adminPage";
	}

	@GetMapping("/shoppingcart")
	public String shoppingCart(Model model) {
		model.addAttribute("nombre", principal.getName());
		// System.out.println("Nombre del usuario autenticado: " + principal.getName());
		model.addAttribute("products",
				userService.getCartProducts(userService.findByUsername(principal.getName()).get()));
		model.addAttribute("totalPrice",
				userService.getTotalPrice(userService.findByUsername(principal.getName()).get()));
		return "shoppingcart";
	}

	@PostMapping("/shoppingcart/{productId}")
	public String addProductToCart(@PathVariable Long productId) {

		User user = userService.findByUsername(principal.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		Product product = productService.getProductById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		userService.addProductToCart(user, product);

		return "redirect:/shoppingcart";
	}

}
