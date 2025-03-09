package es.codeurjc.global_mart.controller;

import java.security.Principal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
<<<<<<< HEAD
=======

>>>>>>> 45624480fc378d8d87ed37539ed89826e375d3bc
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.Review;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.security.CSRFHandlerConfiguration;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import es.codeurjc.global_mart.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {

<<<<<<< HEAD
	private final DaoAuthenticationProvider authenticationProvider;
=======
    private final DaoAuthenticationProvider authenticationProvider;
>>>>>>> 45624480fc378d8d87ed37539ed89826e375d3bc

	private final CSRFHandlerConfiguration CSRFHandlerConfiguration;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private SearchController searchController;

	MainController(CSRFHandlerConfiguration CSRFHandlerConfiguration,
			DaoAuthenticationProvider authenticationProvider) {
		this.CSRFHandlerConfiguration = CSRFHandlerConfiguration;
		this.authenticationProvider = authenticationProvider;
	}

	@ModelAttribute
	public void addAtributes(Model model, HttpServletRequest request, Authentication authentication) {
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			model.addAttribute("logged", true);

			if (principal instanceof OAuth2User oAuth2User) {
				model.addAttribute("username", oAuth2User.getAttribute("name"));
				model.addAttribute("isUser", true);

			} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
				Optional<User> user = userService.findByUsername(userDetails.getUsername());
				model.addAttribute("username", user.get().getUsername());
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
			}
		} else {
			model.addAttribute("logged", false);
		}
	}

	// Functions to redirect to the different pages of the application
	// Initial page (index.html)
	@GetMapping("/")
	public String greeting(Model model) {
		// Obtener los 4 productos más visitados
		List<Product> mostViewedProducts = productService.getMostViewedProducts(4);

		// Convertir las imágenes Blob a Base64 para cada producto
		addImageDataToProducts(mostViewedProducts);

		// Añadir la lista al modelo
		model.addAttribute("mostViewedProducts", mostViewedProducts);

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
		List<Product> products = productService.getNotAcceptedProducts();
		searchController.convertBlobToBase64(products);
		model.addAttribute("productsNotAccepted", products);
		return "administrator";
	}

	// Redirection to the user page
	@GetMapping("/profile")
	public String profile(Model model, Authentication authentication) {
		if (authentication == null) {
			return "redirect:/login"; // Redirigir si no hay usuario autenticado
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof OAuth2User oAuth2User) {
			model.addAttribute("username", oAuth2User.getAttribute("name"));
			model.addAttribute("email", oAuth2User.getAttribute("email"));
			model.addAttribute("profile_image", oAuth2User.getAttribute("picture"));
		}
		// Si el usuario ha iniciado sesión con usuario y contraseña
		else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			Optional<User> user = userService.findByUsername(userDetails.getUsername());
			if (user.isPresent()) {
				model.addAttribute("username", user.get().getUsername());
				model.addAttribute("email", user.get().getEmail());
				model.addAttribute("profile_image", user.get().getImage());
			}
		}

		return "user"; // Nombre del archivo HTML de la vista
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

<<<<<<< HEAD
	@GetMapping("/products/allProducts")
	public String seeAllProds(Model model, HttpServletRequest request) {
		Page<Product> productsPage = productService.getAcceptedProducts(PageRequest.of(0, 5));
		List<Product> products = productsPage.getContent();
		addImageDataToProducts(products);
		model.addAttribute("allProds", products);
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
		model.addAttribute("page", 0);
		return "products";
	}

	@GetMapping("/products/{type}")
	public String getMethodName(@PathVariable String type, Model model) {

		List<Product> products = productService.getAcceptedProductsByType(type);
=======

	@GetMapping("/moreProds")
	public String loadMoreProducts(@RequestParam int page, Model model, HttpServletRequest request) {
		Pageable pageable = PageRequest.of(page, 5);
		Page<Product> productsPage = productService.getAcceptedProducts(pageable);
		List<Product> products = productsPage.getContent();
>>>>>>> 45624480fc378d8d87ed37539ed89826e375d3bc
		addImageDataToProducts(products);
		model.addAttribute("allProds", products);
		model.addAttribute("type", type);
		model.addAttribute("tittle", true);
		return "products";
	}

	@GetMapping("/moreProds")
	public String loadMoreProducts(@RequestParam int page, Model model, HttpServletRequest request) {
		Pageable pageable = PageRequest.of(page, 5);
		Page<Product> productsPage = productService.getAcceptedProducts(pageable);
		List<Product> products = productsPage.getContent();
		addImageDataToProducts(products);

		model.addAttribute("allProds", products);
		return "moreProducts";
	}

	@GetMapping("/product/{id}")
	public String productDescription(@PathVariable Long id, Model model, Authentication autentication)
			throws Exception {
		Optional<Product> product = productService.getProductById(id); // Extract the product by its id

		if (product.isPresent()) {
			// Extract all the info of the product to use it in the musctache template
			model.addAttribute("productName", product.get().getName());
			model.addAttribute("productType", product.get().getType());
			model.addAttribute("productCompany", product.get().getCompany());
			model.addAttribute("productPrice", product.get().getPrice());
			model.addAttribute("productDescription", product.get().getDescription());

			// Convert Blob to Base64 encoded string
			String imageBase64 = null;
			Blob imageBlob = product.get().getImage();
			if (imageBlob != null) {
				byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
				imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
			}

			productService.setViews_product_count(product.get());
			model.addAttribute("productImage", imageBase64);
			model.addAttribute("productId", product.get().getId());
			model.addAttribute("productStock", product.get().getStock());
			model.addAttribute("reviews", product.get().getReviews());

			return "descriptionProduct";
		} else {
			return "redirect:/allProducts";
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
		model.addAttribute("form_title", "New product");
		return "uploadProducts";
	}

	@PostMapping("/newproduct")
	public String newproduct(@RequestParam String product_name, @RequestParam MultipartFile product_image,
			@RequestParam String product_description, @RequestParam String product_type,
			@RequestParam Integer product_stock, @RequestParam Double product_price, Authentication autentication)
			throws Exception {
		Object principal = autentication.getPrincipal();
		// Usamos el parámetro Principal para obtener el nombre del usuario logueado
		if (principal instanceof OAuth2User oAuth2User) {
			productService.createProduct(product_type, product_name, oAuth2User.getAttribute("name"),
					product_price,
					product_description,
					BlobProxy.generateProxy(product_image.getInputStream(), product_image.getSize()),
					product_stock, false);
		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			productService.createProduct(product_type, product_name, userDetails.getUsername(),
					product_price,
					product_description,
					BlobProxy.generateProxy(product_image.getInputStream(), product_image.getSize()),
					product_stock, false);
		}

		return "redirect:/products/allProducts";
	}

	@GetMapping("/acceptProduct/{id}")
	public String acceptProduct(@PathVariable Long id) {
		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent()) {
			productService.updateProduct(id, product.get().getName(),
					product.get().getPrice());
		}
		return "redirect:/adminPage";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return "redirect:/adminPage";
	}

	@GetMapping("/shoppingcart")
	public String shoppingCart(Model model, Authentication autentication) {

		Object principal = autentication.getPrincipal();
		if (principal instanceof OAuth2User oAuth2User) {
			model.addAttribute("username", oAuth2User.getAttribute("name"));
			model.addAttribute("products",
					userService.getCartProducts(userService.findByUsername(oAuth2User.getAttribute("name")).get()));
			model.addAttribute("totalPrice",
					userService.getTotalPrice(userService.findByUsername(oAuth2User.getAttribute("name")).get()));
			// org.springframework.security.web.csrf.CsrfToken csrfToken =
			// CSRFHandlerConfiguration.getToken();
			// model.addAttribute("token", csrfToken.getToken());
		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			Optional<User> user = userService.findByUsername(userDetails.getUsername());
			if (user.isPresent()) {
				model.addAttribute("username", user.get().getUsername());
				model.addAttribute("products", userService.getCartProducts(user.get()));
				model.addAttribute("totalPrice", userService.getTotalPrice(user.get()));
			}
		}

		return "shoppingcart";
	}

	// adds a product to the user cart
	@PostMapping("/shoppingcart/{productId}")
	public String addProductToCart(@PathVariable Long productId, Authentication autentication) {
		Object principal = autentication.getPrincipal();
		if (principal instanceof OAuth2User oAuth2User) {
			User user = userService.findByUsername(oAuth2User.getAttribute("name"))
					.orElseThrow(() -> new RuntimeException("User not found"));
			Product product = productService.getProductById(productId)
					.orElseThrow(() -> new RuntimeException("Product not found"));
			userService.addProductToCart(user, product);
		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			User user = userService.findByUsername(userDetails.getUsername())
					.orElseThrow(() -> new RuntimeException("User not found"));
			Product product = productService.getProductById(productId)
					.orElseThrow(() -> new RuntimeException("Product not found"));
			userService.addProductToCart(user, product);
		}

		return "redirect:/shoppingcart";
	}

	// removes a product from the user cart
	@PostMapping("/removeProductFromCart/{id}")
	public String removeProductFromCart(@PathVariable Long id, Authentication autentication) {
		Object principal = autentication.getPrincipal();
		if (principal instanceof OAuth2User oAuth2User) {
			User user = userService.findByUsername(oAuth2User.getAttribute("name"))
					.orElseThrow(() -> new RuntimeException("User not found"));
			Product product = productService.getProductById(id)
					.orElseThrow(() -> new RuntimeException("Product not found"));
			if (user.getCart().contains(product)) {
				userService.removeProductFromCart(user, product); // call the user method to remove
				// userService.save( user); // update the user in DDBB without this line the
				// user continues rendering the product

			}
		} else {
			return "redirect:/";
		}

		return "redirect:/shoppingcart";
	}

	// to place an order
	@PostMapping("/payment")
	public String payment(Authentication autentication) {
		Object principal = autentication.getPrincipal();

		if (principal instanceof OAuth2User oAuth2User) {
			User user = userService.findByUsername(oAuth2User.getAttribute("name"))
					.orElseThrow(() -> new RuntimeException("User not found"));
			orderService.createOrder(user); // all the payment logic is in the orderService
		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			User user = userService.findByUsername(userDetails.getUsername())
					.orElseThrow(() -> new RuntimeException("User not found"));
			orderService.createOrder(user); // all the payment logic is in the orderService
		}
		return "redirect:/";
	}

	@GetMapping("/edit_product/{id}")
	public String editProductForm(@PathVariable Long id, Model model) {
		model.addAttribute("form_title", "Edit Product");

		Optional<Product> optionalProduct = productService.getProductById(id);
		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();

			// Convertir la imagen Blob a Base64 para mostrarla
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

			// Añadir atributos para seleccionar el tipo correcto en el menú desplegable
			model.addAttribute("type_" + product.getType(), true);
			model.addAttribute("product", product);
		} else {
			return "redirect:/products/allProducts";
		}

		return "uploadProducts";
	}

	@PostMapping("/update_product/{id}")
	public String updateProduct(
			@PathVariable Long id,
			@RequestParam String product_name,
			@RequestParam(required = false) MultipartFile product_image,
			@RequestParam String product_description,
			@RequestParam String product_type,
			@RequestParam Integer product_stock,
			@RequestParam Double product_price,
			Authentication autentication)
			throws Exception {

		Optional<Product> optionalProduct = productService.getProductById(id);
		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();
			product.setName(product_name);
			product.setDescription(product_description);
			product.setType(product_type);
			product.setStock(product_stock);
			product.setPrice(product_price);

			// Actualizar la imagen solo si se proporciona una nueva
			if (product_image != null && !product_image.isEmpty()) {
				product.setImage(BlobProxy.generateProxy(
						product_image.getInputStream(),
						product_image.getSize()));
			}

			productService.addProduct(product);

			// Si el usuario es una empresa, redirigir a sus productos
			Object principal = autentication.getPrincipal();
			if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
				if (userService.findByUsername(userDetails.getUsername()).get().isCompany()) {
					return "redirect:/products/allProducts";
				} else {
					return "redirect:/adminPage";
				}
			} else {
				return "redirect:/products/allProducts";
			}
		}
		return "redirect:/products/allProducts";
	}

	@GetMapping("/displayGraphs")
	public String displayGraph(Model model, Authentication autentication) {

		Object principal = autentication.getPrincipal();
		if (principal instanceof OAuth2User oAuth2User) {
			model.addAttribute("username", oAuth2User.getAttribute("name"));
		}
		if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			Optional<User> user = userService.findByUsername(userDetails.getUsername());
			model.addAttribute("username", user.get().getUsername());

			Map<String, Integer> dataMap = new HashMap<>();

			// Initialize the dataMap with predefined keys and zero values
			dataMap.put("Technology", 0);
			dataMap.put("Books", 0);
			dataMap.put("Education", 0);
			dataMap.put("Sports", 0);
			dataMap.put("Home", 0);
			dataMap.put("Music", 0);
			dataMap.put("Cinema", 0);
			dataMap.put("Appliances", 0);
			dataMap.put("Others", 0);

			// iterate over the products of the company and count the number of products of
			// each type and store it in the dataMap
			List<Product> companyProducts = productService.getAcceptedCompanyProducts(userDetails.getUsername());
			for (Product product : companyProducts) {
				String type = product.getType();
				dataMap.put(type, dataMap.getOrDefault(type, 0) + 1);
			}

			List<Map<String, Object>> dataList = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
				Map<String, Object> item = new HashMap<>();
				item.put("key", entry.getKey());
				item.put("value", entry.getValue());
				dataList.add(item);
			}
			model.addAttribute("dataList", dataList);

			// model.addAttribute("companyProducts", companyProducts);
			model.addAttribute("productsRange", dataList);
			model.addAttribute("books", 12);
			model.addAttribute("username", userDetails.getUsername());
			model.addAttribute("sports", 15);

		}
		return "companyGraphs";
	}

	@GetMapping("/showUserGraphic")
	public String displayUserGraph(Model model, Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (principal instanceof OAuth2User oAuth2User) {
			model.addAttribute("username", oAuth2User.getAttribute("name"));
			List<Double> orderPrices = userService.findByUsername(oAuth2User.getAttribute("name")).get()
					.getHistoricalOrderPrices();
			model.addAttribute("orderPrices", orderPrices);
		}

		return "userGraph";

	}

	// Function to add review to a product
	@PostMapping("/product/{id}/new_review")
	public String postMethodName(@RequestParam int calification, @RequestParam String comment,
			Authentication autentication, @PathVariable Long id) {

		Optional<Product> product = productService.getProductById(id);
		Review review = new Review();
		review.setCalification(calification);
		review.setComment(comment);
		Object principal = autentication.getPrincipal();
		if (principal instanceof OAuth2User oAuth2User) {
			review.setUsername(oAuth2User.getAttribute("name"));
		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			review.setUsername(userDetails.getUsername());
		}

		if (product.isPresent()) {
			product.get().addReview(review);
			productService.addProduct(product.get());
			return "redirect:/product/" + id;
		} else {
			return "redirect:/error";
		}
	}

}