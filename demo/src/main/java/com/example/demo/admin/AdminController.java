package com.example.demo.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {
    @GetMapping("/admin/")
    public String index(Model model){
        model.addAttribute("pageSaatIni","home");
        return "/admin/dashboard";
    }

    @GetMapping("/genre/")
    public String genre(Model model){
        model.addAttribute("pageSaatIni","genre");
        return "/admin/genre";
    }
    
    @GetMapping("/aktor/")
    public String aktor(Model model, @RequestParam( defaultValue = "",required = false) String filter,
     @RequestParam(defaultValue = "1", required = false) Integer page){

        int pageCount = 8;
        model.addAttribute("pageSaatIni","aktor");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "/admin/aktor";
    }
}