package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private HttpSession session;

    @GetMapping("/user")
    public String index(){
        // if (session.getAttribute("idUser") == null) { //TODO: UNCOMMENT TERAKHIR AJA SUPAYA GA HARUS SIGN IN DULU
        //     return "redirect:/signin";
        // }
        //--------------------------------- CEK LOGIN ---------------------------------------
        //BAWAH INI BUAT NGISI HOME PAGE USER (LOGIC CONTROLLER BIASA)

        return "/user/index";
    }
}
