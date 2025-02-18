package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
	@GetMapping("/products")
	public String seeAllProds(Model model) {
		model.addAttribute("homeProds", productService.getProductsByType("Hogar"));
		model.addAttribute("elecProds", productService.getProductsByType("Electronica"));
		model.addAttribute("bookProds", productService.getProductsByType("Libros"));
		model.addAttribute("eduProds", productService.getProductsByType("Educación"));
		model.addAttribute("applianceProds", productService.getProductsByType("Electrodomesticos"));
		model.addAttribute("sportProds", productService.getProductsByType("Deporte"));
		model.addAttribute("musicProds", productService.getProductsByType("Musica"));
		model.addAttribute("movieProds", productService.getProductsByType("Cine"));
		model.addAttribute("otherProds", productService.getProductsByType("Otros"));
		return "products";
	}
}
