package cz.wz.marysidy.spring_genesis_resources.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainWebController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to the web-version!");
        model.addAttribute("title", "Genesis resources");
        model.addAttribute("contentTemplate", "index :: content");
        return "layout";
    }
}
