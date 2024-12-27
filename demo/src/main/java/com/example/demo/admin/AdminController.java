package com.example.demo.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminController {
    @GetMapping("/admin")
    public String index(){
        return "/admin/dashboard";
    }

    @GetMapping("/genre")
    public String genre(){
        return "/admin/genre";
    }
    
    @GetMapping("/aktor")
    public String aktor(){
        return "/admin/aktor";
    }
}
