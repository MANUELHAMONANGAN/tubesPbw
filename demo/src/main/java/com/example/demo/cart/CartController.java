package com.example.demo.cart;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;

import com.example.demo.detailFilm.DetailFilm;
import com.example.demo.detailFilm.DetailFilmRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private DetailFilmRepository detailFilmRepository;

    @Autowired
    private HttpSession session;

    @PostMapping("/cart/add")
    public String addToCart (@RequestParam("filmId") int filmId, @RequestParam(value = "jumlahHari", defaultValue = "1") int jumlahHari, @RequestParam("harga") int hargaPerHari) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        cartRepository.addToCart(idUser, filmId, jumlahHari, hargaPerHari);

        DetailFilm filmDetail = detailFilmRepository.findFilmById(filmId)
            .stream()
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Film not found with id: " + filmId));

        String filmTitle = filmDetail.getJudul();

        // Encode judul film agar sesuai dengan format URL
        String encodedTitle = UriUtils.encodePathSegment(filmTitle, StandardCharsets.UTF_8);

        // redirect ke halaman detail film dengan pola URL `/film/{judul}/{id}` (sama kaya detailFilm)
        return "redirect:/film/" + encodedTitle + "/" + filmId;
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        List<Cart> cartList = cartRepository.findCartByUserId(idUser);
        
        for (Cart cart : cartList) {
            DetailFilm film = detailFilmRepository.findFilmById(cart.getIdFilm())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Film not found with id: " + cart.getIdFilm()));
            cart.setJudul(film.getJudul());
            cart.setCoverBase64(film.getCover());
            cart.setHargaPerHari(film.getHargaPerHari());
        }

        model.addAttribute("cart", cartList);
        int totalHarga = cartList.stream().mapToInt(cart -> cart.getHargaPerHari() * cart.getJumlahHari()).sum();
        model.addAttribute("totalHarga", totalHarga);
        return "cart/cart";
    }

    @GetMapping("/cart/remove/{idFilm}")
    public String removeFromCart(@PathVariable("idFilm") int idFilm) {
        cartRepository.removeFromCart(idFilm);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam("idFilm") int idFilm, @RequestParam("jumlahHari") int jumlahHari) {
        Integer idUser = (Integer) session.getAttribute("idUser");

        // Update jumlahHari dan harga berdasarkan idFilm
        cartRepository.updateCart(idUser, idFilm, jumlahHari);

        return "redirect:/cart"; // Redirect ke halaman cart
    }
}
