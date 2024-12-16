package com.example.demo.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.user.User;
import com.example.demo.user.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    
    
    @GetMapping("/login")
    public String loginView(HttpSession session) {
        if (session.getAttribute("idUser") != null) {
            return "redirect:/user";
        }
        return "/login/index";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model){
        User user = userService.login(email, password);

        if(user != null){
            session.setAttribute("idUser", user.getIdUser());
            session.setAttribute("role", user.getRole());

            if(user.getRole().toString().equals("Pelanggan")){
                return "redirect:/user";
            }else{
                return "redirect:/admin";
            }
        }

        model.addAttribute("status", "failed");
        return "login";
    }

    // Halaman dashboard
    @GetMapping("/user")
    public String homePageUser(HttpSession session) {
        // Jika sesi belum ada, redirect ke login
        if (session.getAttribute("idUser") == null) {
            return "redirect:/login";
        }
        return "/user/index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Hapus semua attribute pada session
        return "redirect:/login";
    }
}
