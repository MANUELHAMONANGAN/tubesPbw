package com.example.demo.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public String index(Model model){
        model.addAttribute("currentPage","home");
        return "/admin/dashboard";
    }

    @GetMapping("/genre")
    public String genre(Model model){
        model.addAttribute("currentPage","genre");
        return "/admin/genre";
    }
    
    @GetMapping("/aktor")
    public String aktor(Model model){
        model.addAttribute("currentPage","aktor");
        return "/admin/aktor";
    }
}
