package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.global_mart.model.User;

// import es.codeurjc.global_mart.model.LoggedUser;
// import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;

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

	// Redirection to see all products
	@GetMapping("/allProducts")
	public String seeAllProds(Model model) {
		model.addAttribute("allProds", productService.getAcceptedProducts());
		model.addAttribute("tittle", false);
		return "products";
	}

	// Redirection to see ONLY the products of a specific type
	@GetMapping("/products/{type}")
	public String getMethodName(@PathVariable String type, Model model) {
								
		model.addAttribute("allProds", productService.getAcceptedProductsByType(type));
		model.addAttribute("type", type);
		model.addAttribute("tittle", true);

		return "products";
	}

	@GetMapping("/product/{id}")
	public String productDescription(@PathVariable Long id, Model model) {
		var product = productService.getProductById(id); // Extract the product by its id
		// Extract all the info of the product to use it in the musctache template
		model.addAttribute("productName", productService.getProductName(product.get()));
		model.addAttribute("productType", productService.getProductType(product.get()));
		model.addAttribute("productCompany", productService.getProductCompany(product.get()));
		model.addAttribute("productPrice", productService.getProductPrice(product.get()));
		model.addAttribute("productDescription", productService.getProductDescription(product.get()));
		model.addAttribute("productImage", productService.getProductImage(product.get()));
		model.addAttribute("productId", productService.getProductId(product.get())); // productos solo si el usuario es
																						// una empresa

		return "descriptionProduct";
	}

	// Redirection to the descriprion of a produdct
	@GetMapping("/descriptionProduct")
	public String descriptionProduct(Model model) {
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
				product_description, product_image, product_stock,false);

		return "redirect:/allProducts";
	}


	@GetMapping("/acceptProduct/{id}")
	public String acceptProduct(@PathVariable Long id) {
		var product = productService.getProductById(id);
		if (product.isPresent()) {
			productService.updateProduct(id, productService.getProductName(product.get()), productService.getProductPrice(product.get()));
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
		return "shoppingcart";

	}
}
