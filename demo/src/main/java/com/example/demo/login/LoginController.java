package com.example.demo.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.user.User;
import com.example.demo.user.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private HttpSession session;
    
    @GetMapping("/signin")
    public String loginView() {
        if (session.getAttribute("idUser") != null) {
            if(session.getAttribute("role").equals("Pelanggan")){
                return "redirect:/";
            }else{
                return "redirect:/admin/";
            }
        }
        return "/login/sign_in";
    }

    @PostMapping("/signin")
    public String processLogin(@RequestParam String email, @RequestParam String password, Model model){
        User user = userService.login(email, password);

        if(user != null){
            session.setAttribute("idUser", user.getIdUser()); //TODO: PAS LOGIN MAU MASUKIN APA AJA?
            session.setAttribute("role", user.getRole().toString());

            if(user.getRole().toString().equals("Pelanggan")){
                return "redirect:/";
            }else{
                return "redirect:/admin/";
            }
        }

        model.addAttribute("status", "failed");
        return "/login/sign_in";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // Hapus semua attribute pada session
        return "redirect:/";
    }
    
    public String loginAdminView() {
        return "halo";
    }
}