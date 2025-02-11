package es.codeurjc.global_mart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    


    @GetMapping("/")
	public String greeting(Model model) {
		return "index";
	}




}
