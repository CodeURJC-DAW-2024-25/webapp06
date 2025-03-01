package es.codeurjc.global_mart.controller;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Blob;
import java.util.Base64;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(required = false) String search_text,
            @RequestParam(required = false, defaultValue = "all") String type,
            Model model) {

        List<Product> searchResults;
        System.out.println("Query: " + search_text);
        System.out.println("Type: " + type);

        if (search_text != null && !search_text.isEmpty()) {
            // Búsqueda por texto
            if ("all".equals(type)) {
                searchResults = productService.searchProductsByName(search_text);
            } else {
                // Búsqueda por texto y categoría
                searchResults = productService.searchProductsByNameAndType(search_text, type);
            }
        } else if (!"all".equals(type)) {
            // Sólo búsqueda por categoría
            searchResults = productService.getProductsByType(type);
        } else {
            // Sin criterios, mostrar todos los productos
            searchResults = productService.getAllProducts();
        }

        // Convertir las imágenes Blob a Base64 para cada producto
        convertBlobToBase64(searchResults);

        model.addAttribute("products", searchResults);
        model.addAttribute("searchQuery", search_text);
        model.addAttribute("category", type);

        return "search"; // Template que mostrará los resultados
    }

    private void convertBlobToBase64(List<Product> products) {
        for (Product product : products) {
            try {
                Blob imageBlob = product.getImage();
                if (imageBlob != null) {
                    byte[] bytes = imageBlob.getBytes(1, (int) imageBlob.length());
                    String imageBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
                    product.setImageBase64(imageBase64);
                } else {
                    // Imagen por defecto si es null
                    product.setImageBase64("/images/default-product.jpg");
                }
            } catch (Exception e) {
                e.printStackTrace();
                product.setImageBase64("/images/default-product.jpg");
            }
        }
    }
}