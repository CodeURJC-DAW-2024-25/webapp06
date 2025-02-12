package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    

//Functions to redirect to the different pages of the application
	//Initial page (index.html)
    @GetMapping("/")
	public String greeting(Model model) {
		return "index";
	}
	//Redirection to the initial page
	@GetMapping("/redir")
	public String redir(Model model) {
		return "redirect:/";
	}
	//Redirection to the login page
	@GetMapping("/choose_login_option")
	public String choose_login(Model model) {
		return "choose_login_option";
	}
	//Redirection to the register page
	@GetMapping("/register")
	public String register(Model model) {
		return "register";
	}
	//Redirection to the login page
	@GetMapping("/login")
	public String login(Model model) {
		return "login";
	}





//Redirection to see al prods (NEED UPDATE)
	@GetMapping("/seeAllProds")
	public String seeAllProds(Model model) {
		return "products";
	}

}
