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
    public String seeAllProds(Model model, HttpServletRequest request, Authentication authentication) {
        List<Product> products = productService.getAcceptedProducts(PageRequest.of(0, 5)).getContent();
        addImageDataToProducts(products);
        model.addAttribute("allProds", products);
        model.addAttribute("tittle", false);
        model.addAttribute("hasNextProd", productService.getAcceptedProducts(PageRequest.of(1, 5)).hasContent());

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            model.addAttribute("allCompanyProds", Collections.emptyList());
        } else {
            Optional<User> user = userService.findByUsername(principal.getName());
            if (user.isPresent() && user.get().isCompany()) {
                List<Product> companyProducts = productService.getAcceptedCompanyProducts(user.get().getUsername(), PageRequest.of(0, 5)).getContent();
                addImageDataToProducts(companyProducts);
                model.addAttribute("companyName", user.get().getName());
                model.addAttribute("allCompanyProds", companyProducts);
            } else {
                model.addAttribute("allCompanyProds", Collections.emptyList());
            }
        }
        return "products";
    }

    @GetMapping("/products/{type}")
    public String getMethodName(@PathVariable String type, Model model) {

        List<Product> products = productService.getAcceptedProductsByType(type, PageRequest.of(0, 5)).getContent();
        addImageDataToProducts(products);
        model.addAttribute("allProds", products);
        model.addAttribute("type", type);
        model.addAttribute("tittle", true);
        model.addAttribute("hasNextProd", productService.getAcceptedProductsByType(type, PageRequest.of(1, 5)).hasContent());
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
}
