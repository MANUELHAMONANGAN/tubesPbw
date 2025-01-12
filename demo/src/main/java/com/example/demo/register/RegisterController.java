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

    @GetMapping("/signup")
    public String registerView(User user){
        return "/register/sign_up";
    }

    @PostMapping("/signup")
    public String register(@Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){ //input ga valid
            return "/register/sign_up";
        }

        if(!userService.signUpCekNoTelp(user)){ //nomor telepon sudah terdaftar
            bindingResult.rejectValue("nomorTelepon", "FailedQuery", "Nomor Telepon sudah terdaftar. Gunakan nomor telepon lain");
            return "/register/sign_up";
        }

        if(!userService.signUpCekEmail(user)){ //input semua valid, tapi gagal eksekusi query
            bindingResult.rejectValue("email", "FailedQuery", "Email sudah terdaftar. Gunakan email lain");
            return "/register/sign_up";
        }

        return "/register/result";
    }
}