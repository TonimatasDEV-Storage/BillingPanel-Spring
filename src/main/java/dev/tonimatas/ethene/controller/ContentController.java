package dev.tonimatas.ethene.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("index")
    public String index() {
        return "redirect:/";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
