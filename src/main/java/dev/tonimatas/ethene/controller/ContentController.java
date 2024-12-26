package dev.tonimatas.ethene.controller;

import dev.tonimatas.ethene.users.EtheneUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String home(@AuthenticationPrincipal EtheneUser user, Model model) {
        model.addAttribute("firstname", user.getFirstname());
        model.addAttribute("lastname", user.getLastname());
        return "index";
    }

    @GetMapping("/verify")
    public String verify() {
        return "verify";
    }
}
