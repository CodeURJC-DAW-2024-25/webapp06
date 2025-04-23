package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RouteController {
    @RequestMapping("/new/**")
    public String redirectAngularRoutes() {
        return "forward:/new/index.html";
    }
}
