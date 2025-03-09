package es.codeurjc.global_mart.controller;

import java.security.Principal;
import java.sql.Blob;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProductsController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

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

    @GetMapping("/products/allProducts")
    public String seeAllProds(
            Model model,
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<Product> productsPage = productService.getAcceptedProducts(PageRequest.of(page, size));
        List<Product> products = productsPage.getContent();
        addImageDataToProducts(products);

        model.addAttribute("allProds", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("tittle", false);

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            model.addAttribute("allCompanyProds", Collections.emptyList());
        } else {
            Optional<User> user = userService.findByUsername(principal.getName());
            if (user.isPresent() && user.get().isCompany()) {
                model.addAttribute("allCompanyProds",
                        productService.getAcceptedCompanyProducts(user.get().getUsername(),
                                PageRequest.of(page, size)));
            }
        }

        return "products";
    }

    @GetMapping("/products/{type}")
    public String seeCategorizedProds(
            @PathVariable String type,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<Product> productsPage = productService.getAcceptedProductsByType(type, PageRequest.of(page, size));
        List<Product> products = productsPage.getContent();
        addImageDataToProducts(products);

        model.addAttribute("allProds", products);
        model.addAttribute("type", type);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productsPage.getTotalPages());
        model.addAttribute("tittle", true);

        return "products";
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
}
