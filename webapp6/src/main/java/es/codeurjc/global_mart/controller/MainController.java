package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.global_mart.service.ProductService;

@Controller
public class MainController {

	@Autowired
	private ProductService productService;

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

	// Redirection to the register page
	@GetMapping("/register")
	public String register(Model model) {
		return "register";
	}

	// Redirection to the login page
	@GetMapping("/login")
	public String login(Model model) {
		return "login";
	}

	// Redirection to the about us page
	@GetMapping("/about")
	public String about_us(Model model) {
		return "about";
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
		return "descriptionProduct";
	}

}
