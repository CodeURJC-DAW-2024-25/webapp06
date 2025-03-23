package es.codeurjc.global_mart.controller;

import java.security.Principal;
import java.sql.Blob;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.global_mart.dto.Product.ProductDTO;
import es.codeurjc.global_mart.dto.User.UserDTO;
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

    @GetMapping("/products/allProducts")
    public String seeAllProds(Model model, HttpServletRequest request, Authentication authentication) {
        List<ProductDTO> products = productService.getAcceptedProducts(PageRequest.of(0, 5)).getContent();
        productService.addImageDataToProducts(products);
        model.addAttribute("allProds", products);
        model.addAttribute("tittle", false);
        model.addAttribute("hasNextProd", productService.getAcceptedProducts(PageRequest.of(1, 5)).hasContent());

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            model.addAttribute("allCompanyProds", Collections.emptyList());
        } else {
            Optional<UserDTO> user = userService.findByUsername(principal.getName());
            if (user.isPresent() && userService.isCompany(user.get())) {
                List<ProductDTO> companyProducts = productService
                        .getAcceptedCompanyProducts(user.get().username(), PageRequest.of(0, 5)).getContent();
                productService.addImageDataToProducts(companyProducts);
                model.addAttribute("companyName", user.get().name());
                model.addAttribute("allCompanyProds", companyProducts);
            } else {
                model.addAttribute("allCompanyProds", Collections.emptyList());
            }
        }
        return "products";
    }

    @GetMapping("/products/{type}")
    public String getMethodName(@PathVariable String type, Model model) {

        List<ProductDTO> products = productService.getAcceptedProductsByType(type, PageRequest.of(0, 5)).getContent();
        productService.addImageDataToProducts(products);
        model.addAttribute("allProds", products);
        model.addAttribute("type", type);
        model.addAttribute("tittle", true);
        model.addAttribute("hasNextProd",
                productService.getAcceptedProductsByType(type, PageRequest.of(1, 5)).hasContent());
        return "products";
    }

    @GetMapping("/product/{id}")
    public String productDescription(@PathVariable Long id, Model model, Authentication autentication)
            throws Exception {
        Optional<ProductDTO> product = productService.getProductById(id); // Extract the product by its id

        if (product.isPresent()) {
            // Extract all the info of the product to use it in the musctache template
            model.addAttribute("productName", product.get()); // product dto contains all the product info review html

            // Convert Blob to Base64 encoded string
            String imageBase64 = null;
            // Blob imageBlob = product.get().image();
            // if (imageBlob != null) {
            // byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
            // imageBase64 = "data:image/jpeg;base64," +
            // Base64.getEncoder().encodeToString(bytes);
            // }

            productService.setViews_product_count(product.get());
            model.addAttribute("productImage", imageBase64);
            model.addAttribute("productId", product.get().id());
            model.addAttribute("productStock", product.get().stock());
            model.addAttribute("reviews", product.get().reviews());

            return "descriptionProduct";
        } else {
            return "redirect:/allProducts";
        }
    }

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
        Optional<ProductDTO> product = productService.getProductById(id);
        if (product.isPresent()) {
            productService.updateProduct(id, product.get().name(),
                    product.get().price());
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

        Optional<ProductDTO> optionalProduct = productService.getProductById(id);
        if (optionalProduct.isPresent()) {
            // Product product = optionalProduct.get();
            userService.convertBlobToBase64(optionalProduct.get());

            // add the product to the model
            model.addAttribute("type_" + optionalProduct.get().type(), true);
            model.addAttribute("product", optionalProduct.get());
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

        Optional<ProductDTO> optionalProduct = productService.getProductById(id);
        if (optionalProduct.isPresent()) {
            productService.updateProductDetails(optionalProduct.get(), product_name, product_description, product_type,
                    product_stock, product_price, product_image);

            Object principal = autentication.getPrincipal();
            if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
                Optional<UserDTO> user = userService.findByUsername(userDetails.getUsername());
                if (user.isPresent() && userService.isCompany(user.get())) {
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
}
