package com.example.demo.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    
    @GetMapping("/signin")
    public String loginView() {
        return "/login/sign_in";
    }

    @GetMapping("/signup")
    public String signUpView() {
        return "/login/sign_up";
    }
    
    public String loginAdminView() {
        return "halo";
    }
}
