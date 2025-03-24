package es.codeurjc.global_mart.controller.API_Rest;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.global_mart.dto.Product.CompanyStadsDTO;
import es.codeurjc.global_mart.dto.User.HistoricalOrdersDTO;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;

@RestController
@RequestMapping("api/graphsController")
public class APIGraphsController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("/companyGraph")
    public ResponseEntity<?> displayGraph(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username = null;

        if (principal instanceof OAuth2User oAuth2User) {
            username = oAuth2User.getAttribute("name");
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            username = userDetails.getUsername();
        }

        if (username != null) {
            try {
                List<CompanyStadsDTO> dataList = productService.getCompanyStadistics(username);
                return ResponseEntity.ok(dataList);
            } catch (NoSuchElementException e) {
                return ResponseEntity.status(404).body("Error al devolver los datos");
            }
        }

        return ResponseEntity.status(401).body("Unauthorized");
    }
    
    @GetMapping("/userGraph")
    public ResponseEntity<?> displayUserGraph(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String userName = null;

        if (principal instanceof OAuth2User oAuth2User) {
            userName = oAuth2User.getAttribute("name");
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            userName = userDetails.getUsername();
        }

        if (userName != null) {
            try {
                HistoricalOrdersDTO orderPrices = userService.getUserStads(userName);
                return ResponseEntity.ok(orderPrices.historicalOrdersPrices());
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error al obtener los datos");
            }
        }

        return ResponseEntity.status(401).body("Unauthorized");
    }

}



