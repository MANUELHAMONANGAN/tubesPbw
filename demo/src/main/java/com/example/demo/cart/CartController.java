package com.example.demo.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private HttpSession session;

    @PostMapping("/cart/add")
    public String addToCart (@RequestParam("filmId") int filmId, @RequestParam(value = "jumlahHari", defaultValue = "1") int jumlahHari) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        cartRepository.addToCart(idUser, filmId, jumlahHari);
        return "redirect:/film/" + filmId;
    }

    @GetMapping("/cart")
    public String viewCart (Model model) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        List<Cart> cart = cartRepository.findCartByUserId(idUser);
        model.addAttribute("cart", cart);
        return "cart";
    }

    
}
