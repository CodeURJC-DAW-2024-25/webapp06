package es.codeurjc.global_mart.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.global_mart.dto.Product.CompanyStadsDTO;
import es.codeurjc.global_mart.dto.User.HistoricalOrdersDTO;
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

            try {

                List<CompanyStadsDTO> dataList = productService.getCompanyStadistics(userDetails.getUsername());
                model.addAttribute("dataList", dataList);
                System.out.println("Company stadistics" + dataList);
                return "companyGraphs";

            } catch (NoSuchElementException e) {
                System.out.println("Error al devolver los datos");
                return "error";
            }
        }

        return "error";

    }

    @GetMapping("/showUserGraphic")
    public String displayUserGraph(Model model, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            model.addAttribute("username", oAuth2User.getAttribute("name"));
            HistoricalOrdersDTO orderPrices = userService.getUserStads(oAuth2User.getName().toString());
            model.addAttribute("orderPrices", orderPrices);
        }
        return "userGraph";
    }

}
