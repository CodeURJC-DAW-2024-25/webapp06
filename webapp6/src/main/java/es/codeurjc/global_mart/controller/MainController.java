package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

<<<<<<< HEAD
import es.codeurjc.global_mart.model.LoggedUser;
import es.codeurjc.global_mart.repository.UserRepository;
=======
import es.codeurjc.global_mart.model.User;
>>>>>>> a48f5954db2682325d8d1b309c22634551df1620
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
		User user = userService.findByUsername(username);

		model.addAttribute("username", user.getUsername());
		model.addAttribute("email", user.getEmail());
		model.addAttribute("profile_image", user.getImage());

		return "user";
	}

	// Redirection to see all products
	@GetMapping("/allProducts")
	public String seeAllProds(Model model) {
		Map<String, Object> data = new HashMap<>(); // Creation of a map to pass the boolean values to the mustache
													// template
		data.put("Hogar", true);
		data.put("Electronica", true);
		data.put("Libros", true);
		data.put("Educacion", true);
		data.put("Electrodomesticos", true);
		data.put("Deporte", true);
		data.put("Musica", true);
		data.put("Cine", true);
		data.put("Otros", true);
		// Add all the products scanning them by type of product
		model.addAttribute("HogarProds", productService.getAcceptedProductsByType("Hogar"));
		model.addAttribute("ElectronicaProds", productService.getProductsByType("Electronica"));
		model.addAttribute("LibrosProds", productService.getProductsByType("Libros"));
		model.addAttribute("EducacionProds", productService.getProductsByType("Educación"));
		model.addAttribute("ElectrodomesticosProds", productService.getProductsByType("Electrodomesticos"));
		model.addAttribute("DeporteProds", productService.getProductsByType("Deporte"));
		model.addAttribute("MusicaProds", productService.getProductsByType("Musica"));
		model.addAttribute("CineProds", productService.getProductsByType("Cine"));
		model.addAttribute("OtrosProds", productService.getProductsByType("Otros"));
		model.addAttribute("boolean", data);

		return "products";
	}

	// Redirection to see ONLY the products of a specific type
	@GetMapping("/{type}")
	public String getMethodName(@PathVariable String type, Model model) {
		Map<String, Object> data = new HashMap<>(); // Creation of a map to pass the boolean (make dissapear the titles
													// of the other types)
		data.put(type, true);
		model.addAttribute(type + "Prods", productService.getProductsByType(type));
		model.addAttribute("boolean", data);
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
				product_description, product_image, product_stock);

		return "redirect:/allProducts";
	}

	@GetMapping("/shoppingcart")
	public String shoppingCart(Model model) {
		return "shoppingcart";

	}
}
