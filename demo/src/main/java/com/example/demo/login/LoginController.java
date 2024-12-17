package com.example.demo.login;

import java.util.Optional;

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
    
    @GetMapping("/login")
    public String loginView(User user) {
        if (session.getAttribute("idUser") != null) {
            
            if(session.getAttribute("role").equals("Pelanggan")){
                return "redirect:/user";
            }else{
                return "redirect:/admin"; //TODO: GANTI KE HOMEPAGE ADMIN
            }
        }
        return "/login/index";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password, Model model){
        Optional<User> user = userService.login(email, password);

        if(user.isPresent()){
            session.setAttribute("idUser", user.get().getIdUser()); //TODO: PAS LOGIN MAU MASUKIN APA AJA?
            session.setAttribute("role", user.get().getRole().toString());

            if(user.get().getRole().toString().equals("Pelanggan")){
                return "redirect:/user";
            }else{
                return "redirect:/admin"; //TODO: GANTI KE HOMEPAGE ADMIN
            }
        }

        model.addAttribute("status", "failed");
        model.addAttribute("user", Optional.empty());
        return "/login/index";
    }

    @GetMapping("/logout")
    public String logout() {
        session.invalidate(); // Hapus semua attribute pada session
        return "redirect:/";
    }
}
