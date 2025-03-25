package es.codeurjc.global_mart.controller.API_Rest;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.global_mart.dto.Product.CompanyStadsDTO;
import es.codeurjc.global_mart.dto.User.HistoricalOrdersDTO;
import es.codeurjc.global_mart.service.ProductService;
import es.codeurjc.global_mart.service.UserService;

@RestController
@RequestMapping("/api/graphs")
public class APIGraphsController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping("/companyGraph")
    public ResponseEntity<?> displayGraph(Authentication authentication) {
        String username = getUsername(authentication);
        if (username == null) {
            return ResponseEntity.status(401).body("Unauthorized: Usuario no autenticado");
        }

        try {
            List<CompanyStadsDTO> dataList = productService.getCompanyStadistics(username);
            return ResponseEntity.ok(dataList);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Error: No se encontraron estadísticas para el usuario");
        }
    }

   
    @GetMapping("/userGraph")
    public ResponseEntity<?> displayUserGraph(Authentication authentication) {
        String username = getUsername(authentication);
        if (username == null) {
            return ResponseEntity.status(401).body("Unauthorized: Usuario no autenticado");
        }

        try {
            HistoricalOrdersDTO orderPrices = userService.getUserStads(username);
            return ResponseEntity.ok(orderPrices.historicalOrdersPrices());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener los datos del usuario");
        }
    }

    private String getUsername(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            return oAuth2User.getAttribute("name");
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            return userDetails.getUsername();
        }

        return null;
    }

    
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(404).body("Error: No se encontraron datos");
    }
}
