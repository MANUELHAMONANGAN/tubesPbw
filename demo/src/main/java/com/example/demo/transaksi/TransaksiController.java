package com.example.demo.transaksi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.transaksiFilm.TransaksiFilmRequest;

@RestController
@RequestMapping("/transaksi")
public class TransaksiController {
    
    private List<TransaksiFilmRequest> temporaryCart = new ArrayList<>();

    @PostMapping("/temp")
    public ResponseEntity<?> addToTempCart(@RequestBody TransaksiFilmRequest request) {
        temporaryCart.add(request);
        return ResponseEntity.ok(temporaryCart);
    }

    @GetMapping("/temp")
    public ResponseEntity<?> getTempCart() {
        return ResponseEntity.ok(temporaryCart);
    }

    @DeleteMapping("/temp/{idFilm}")
    public ResponseEntity<?> removeFromTempCart(@PathVariable int idFilm) {
        temporaryCart.removeIf(item -> item.getIdFilm() == idFilm);
        return ResponseEntity.ok(temporaryCart);
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransaksiRequest transaksiRequest) {
        // Simpan transaksi dan detailnya ke database
        // Panggil service yang berisi logika penyimpanan
        return ResponseEntity.ok("Transaksi berhasil dibuat");
    }
}
