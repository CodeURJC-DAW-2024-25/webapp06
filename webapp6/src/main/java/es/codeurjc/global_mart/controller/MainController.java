package es.codeurjc.global_mart.controller;

import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
=======
<<<<<<< HEAD
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.security.CSRFHandlerConfiguration;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;

@Controller
public class MainController {

	private final CSRFHandlerConfiguration CSRFHandlerConfiguration;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private SearchController searchController;

	MainController(CSRFHandlerConfiguration CSRFHandlerConfiguration, DaoAuthenticationProvider authenticationProvider) {
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


	@GetMapping("/moreProds")
	public String loadMoreProducts(@RequestParam int page, Model model, HttpServletRequest request) {
		Pageable pageable = PageRequest.of(page, 5);
		Page<Product> productsPage = productService.getAcceptedProducts(pageable);
		List<Product> products = productsPage.getContent();
		addImageDataToProducts(products);

		model.addAttribute("allProds", products);
		model.addAttribute("type", type);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", productsPage.getTotalPages());
		model.addAttribute("tittle", true);

		return "products";
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

>>>>>>> parent of a667434 (controller imports)
}
