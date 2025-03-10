package es.codeurjc.global_mart.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.global_mart.model.Product;
import es.codeurjc.global_mart.model.User;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;

@Controller
public class GraphsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

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

}
