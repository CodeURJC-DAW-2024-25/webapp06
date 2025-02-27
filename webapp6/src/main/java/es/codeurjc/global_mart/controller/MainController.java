package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import es.codeurjc.global_mart.model.LoggedUser;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.ProductService;
import jakarta.servlet.http.HttpSession;

import java.sql.Blob;
import java.util.Base64;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {

	@Autowired
	private ProductService productService;

	@Autowired
	private LoggedUser loggedUser;

	// Functions to redirect to the different pages of the application
	// Initial page (index.html)
	@GetMapping("/")
	public String greeting(Model model) {
		User user = loggedUser.getUser();
		if (user != null && user.getUsername() != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("company", loggedUser.isCompany()); // para que en la vista se muestre el botón de subir
																	// productos solo si el usuario es una empresa
		}
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
		User user = loggedUser.getUser();
		if (user != null && user.getUsername() != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("company", loggedUser.isCompany()); // para que en la vista se muestre el botón de subir
																	// productos solo si el usuario es una empresa
		}
		return "about";
	}

	// Redirection to the user page
	@GetMapping("/profile")
	public String profile(Model model) {
		User user = loggedUser.getUser(); // Obtiene el usuario logueado
		if (user != null && user.getUsername() != null) {
			if (user.getImage() != null) { // Si el usuario tiene una imagen de perfil
				try {
					Blob blob = user.getImage(); // Obtiene la imagen
					byte[] imageBytes = blob.getBytes(1, (int) blob.length()); // Convierte la imagen a bytes
					String base64Image = Base64.getEncoder().encodeToString(imageBytes); // Codifica la imagen en base64
																							// en base64 ya que el
																							// navegador no permite
																							// representar archivos Blob
					model.addAttribute("profile_image", "data:image/jpeg;base64," + base64Image);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				model.addAttribute("profile_image", "ruta/a/imagen/por/defecto.png");
			}
			model.addAttribute("name", user.getName());
			model.addAttribute("username", user.getUsername());
			model.addAttribute("email", user.getEmail());
		}
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
		model.addAttribute("HogarProds", productService.getProductsByType("Hogar"));
		model.addAttribute("ElectronicaProds", productService.getProductsByType("Electronica"));
		model.addAttribute("LibrosProds", productService.getProductsByType("Libros"));
		model.addAttribute("EducacionProds", productService.getProductsByType("Educación"));
		model.addAttribute("ElectrodomesticosProds", productService.getProductsByType("Electrodomesticos"));
		model.addAttribute("DeporteProds", productService.getProductsByType("Deporte"));
		model.addAttribute("MusicaProds", productService.getProductsByType("Musica"));
		model.addAttribute("CineProds", productService.getProductsByType("Cine"));
		model.addAttribute("OtrosProds", productService.getProductsByType("Otros"));
		model.addAttribute("boolean", data);
		User user = loggedUser.getUser();
		if (user != null && user.getUsername() != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("company", loggedUser.isCompany()); // para que en la vista se muestre el botón de subir
																	// productos solo si el usuario es una empresa
		}
		
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
		User user = loggedUser.getUser();
		if (user != null && user.getUsername() != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("company", loggedUser.isCompany()); // para que en la vista se muestre el botón de subir
																	// productos solo si el usuario es una empresa
		}
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
		model.addAttribute("productId", productService.getProductId(product.get()));
		User user = loggedUser.getUser();
		if (user != null && user.getUsername() != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("company", loggedUser.isCompany()); // para que en la vista se muestre el botón de subir
																	// productos solo si el usuario es una empresa
		}
		return "descriptionProduct";
	}

	// Redirection to the descriprion of a produdct
	@GetMapping("/descriptionProduct")
	public String descriptionProduct(Model model) {
		User user = loggedUser.getUser();
		if (user != null && user.getUsername() != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("company", loggedUser.isCompany()); // para que en la vista se muestre el botón de subir
																	// productos solo si el usuario es una empresa
		}
		return "descriptionProduct";
	}

	@GetMapping("/new_product")
	public String new_product(Model model) {
		User user = loggedUser.getUser();
		if (user != null && user.getUsername() != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("company", loggedUser.isCompany()); // para que en la vista se muestre el botón de subir
																	// productos solo si el usuario es una empresa
		}
		return "uploadProducts";
	}

	@PostMapping("/newproduct")
	public String newproduct(@RequestParam String product_name, @RequestParam MultipartFile product_image,
			@RequestParam String product_description, @RequestParam String product_type,
			@RequestParam Integer product_stock, @RequestParam Double product_price) throws Exception {
		User user = loggedUser.getUser(); // Obtiene el usuario logueado
		if (user != null && user.getUsername() != null) {
			String product_business = user.getUsername();
			productService.createProduct(product_type, product_name, product_business, product_price,
					product_description, product_image, product_stock);
		}
		return "redirect:/allProducts";
	}

	@GetMapping("shoppingCart/{id}")
	public String shoppingCart(@PathVariable Long id, Model model) {
		User user = loggedUser.getUser();
		if (user != null && user.getUsername() != null) {
			model.addAttribute("username", user.getUsername());
			model.addAttribute("company", loggedUser.isCompany()); // para que en la vista se muestre el botón de subir
																	// productos solo si el usuario es una empresa
		}
		return "shoppingCart";

	}
}
