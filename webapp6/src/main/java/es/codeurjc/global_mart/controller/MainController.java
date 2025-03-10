package es.codeurjc.global_mart.controller;

import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private SearchController searchController;

	@GetMapping("/")
	public String greeting(Model model) {
		// get the most viewed and last products
		List<Product> mostViewedProducts = productService.getMostViewedProducts(4);
		List<Product> lastProducts = productService.getLastProducts(4);

		// Add image data to the products
		addImageDataToProducts(mostViewedProducts);
		addImageDataToProducts(lastProducts);

		// Add the products to the model
		model.addAttribute("mostViewedProducts", mostViewedProducts);
		model.addAttribute("lastProducts", lastProducts);

		return "index";
	}

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
		List<Product> products = productService.getNotAcceptedProducts();
		searchController.convertBlobToBase64(products);
		model.addAttribute("productsNotAccepted", products);
		return "administrator";
	}

	@GetMapping("/profile")
	public String profile(Model model, Authentication authentication) {
		if (authentication == null) {
			return "redirect:/login";
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof OAuth2User oAuth2User) {
			model.addAttribute("name", oAuth2User.getAttribute("name"));
			model.addAttribute("username", oAuth2User.getAttribute("name"));
			model.addAttribute("email", oAuth2User.getAttribute("email"));
			model.addAttribute("profile_image", oAuth2User.getAttribute("picture"));
			model.addAttribute("isGoogleUser", true); // add flag to indicate authentication with Google
		}
		// Regular user
		else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			Optional<User> user = userService.findByUsername(userDetails.getUsername());
			if (user.isPresent()) {
				model.addAttribute("name", user.get().getName());
				model.addAttribute("username", user.get().getUsername());
				model.addAttribute("email", user.get().getEmail());
				model.addAttribute("profile_image", user.get().getImage());
				model.addAttribute("isGoogleUser", false); // add flag to indicate authentication with username and
															// password
			}
		}

		return "user";
	}

	private void addImageDataToProducts(List<Product> products) {
		for (Product product : products) {
			try {
				Blob imageBlob = product.getImage();
				if (imageBlob != null) {
					byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
					String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
					product.setImageBase64(imageBase64);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping("/moreProdsAll")
	public String loadMoreProducts(@RequestParam int page, Model model, HttpServletRequest request) {
		Pageable pageable = Pageable.ofSize(5).withPage(page);
		Page<Product> productsPage = productService.getAcceptedProducts(pageable);
		List<Product> products = productsPage.getContent();
		addImageDataToProducts(products);
		model.addAttribute("hasMore", productsPage.getTotalPages() - 1 > page);
		model.addAttribute("allProds", products);
		return "moreProducts";
	}

	@GetMapping("/moreProdsTypes")
	public String loadMoreProductsByType(@RequestParam int page, @RequestParam String type, Model model) {
		Pageable pageable = Pageable.ofSize(5).withPage(page);
		Page<Product> productsPage = productService.getAcceptedProductsByType(type, pageable);
		List<Product> products = productsPage.getContent();
		addImageDataToProducts(products);
		model.addAttribute("hasMore", productsPage.getTotalPages() - 1 > page);
		model.addAttribute("allProds", products);
		model.addAttribute("type", type);
		return "moreProducts";
	}

	@GetMapping("/moreProdsCompany")
	public String loadMoreProductsByCompany(@RequestParam int page, @RequestParam String company, Model model) {
		Pageable pageable = Pageable.ofSize(5).withPage(page);
		Page<Product> productsPage = productService.getAcceptedCompanyProducts(company, pageable);
		List<Product> products = productsPage.getContent();
		addImageDataToProducts(products);

		model.addAttribute("hasMore", productsPage.getTotalPages() - 1 > page);
		model.addAttribute("allProds", products);
		model.addAttribute("company", company);
		model.addAttribute("isCompany", true);
		return "moreProducts";
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public SearchController getSearchController() {
		return searchController;
	}

	public void setSearchController(SearchController searchController) {
		this.searchController = searchController;
	}

}