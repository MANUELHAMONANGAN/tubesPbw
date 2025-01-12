package com.example.demo.transaksi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.cart.Cart;
import com.example.demo.cart.CartService;
import com.example.demo.enums.MethodBayarEnum;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserTransaksiController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private HttpSession session;
    
    @PostMapping("/transaksi/process")
    public ResponseEntity<String> processTransaksi(@RequestParam String metodePembayaran) {
        try {
            Integer idUser = (Integer) session.getAttribute("idUser");
            if (idUser == null) {
                return ResponseEntity.badRequest().body("User belum login");
            }
            
            // Ambil item dari cart
            List<Cart> cartItems = cartService.getCartByUserId(idUser);
            if (cartItems.isEmpty()) {
                return ResponseEntity.badRequest().body("Keranjang kosong");
            }
            
            MethodBayarEnum metode = MethodBayarEnum.valueOf(metodePembayaran.toUpperCase());
            // Buat transaksi dengan status menunggu persetujuan
            Transaksi transaksi = cartService.createTransaki(idUser, cartItems, metode);
            
            // Kosongkan cart setelah transaksi dibuat
            cartService.clearCartByUserId(idUser);
            
            return ResponseEntity.ok("Transaksi berhasil dibuat dan menunggu persetujuan admin");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Terjadi kesalahan: " + e.getMessage());
        }
    }
}