package com.nishant.assignment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/register")
    public String root() {
        return "redirect:/register.html";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    @GetMapping("/docs")
    public String docs() {
        return "redirect:/swagger-ui/index.html";
    }

    @GetMapping({"/", "/index", "/home"})
    public String home() {
        return "redirect:/index.html";
    }
}