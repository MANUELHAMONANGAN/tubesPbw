package com.example.demo.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.user.User;
import com.example.demo.user.UserService;

import jakarta.validation.Valid;

@Controller
public class RegisterController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){ //input ga valid
            return "/login/index";
        }

        if(!userService.signUp(user)){ //input semua valid, tapi gagal eksekusi query
            bindingResult.rejectValue("email", "FailedQuery", "Data duplikat (email sudah terdaftar pada database)");
            return "/login/index";
        }

        return "/login/result";
    }
}
