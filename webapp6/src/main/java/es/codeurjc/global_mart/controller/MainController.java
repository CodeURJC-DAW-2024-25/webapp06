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
		model.addAttribute("products", productService.getAllProducts());
		return "products";
	}


	// Redirection to see ONLY the products of a specific type
	@GetMapping("/{type}")
	public String getMethodName(@PathVariable String type, Model model) {
		model.addAttribute("categorized", true);
		model.addAttribute("products", productService.getProductsByType(type));
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


	// Redirection to the descriprion of a produdct
	@GetMapping("/descriptionProduct")
	public String descriptionProduct(Model model) {
		return "descriptionProduct";
	}
}
