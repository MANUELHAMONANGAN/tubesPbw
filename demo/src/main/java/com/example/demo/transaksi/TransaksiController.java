package com.example.demo.transaksi;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.transaksiFilm.TransaksiFilm;

@Controller
@RequestMapping("/admin/kelolatransaksi")
public class TransaksiController {

    @Autowired
    private TransaksiService transaksiService;

    @GetMapping
    public String viewTransaksiList(Model model) {
        List<Transaksi> transaksiList = transaksiService.getAllTransaksi();
        model.addAttribute("transaksiList", transaksiList);
        return "admin/kelolatransaksi";
    }

    @PostMapping("/approve")
    public String approveTransaksi(@RequestParam int idTransaksi) {
        transaksiService.approveTransaksi(idTransaksi);
        return "redirect:/admin/kelolatransaksi";
    }

    @PostMapping("/detail/{idTransaksi}/{idFilm}/updatestatus")
    public String updateStatus(@PathVariable int idTransaksi, @PathVariable int idFilm) {
        transaksiService.updateFilmStatus(idTransaksi, idFilm);
        transaksiService.updateTypeIfAllDone(idTransaksi);
        return "redirect:/admin/kelolatransaksi/detail/{idTransaksi}";
    }

    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable int id, Model model) {
        List<TransaksiFilm> transaksiFilms = transaksiService.getTransaksiFilmsByTransaksiId(id);
        
        Optional<Transaksi> transaksiOpt = transaksiService.getAllTransaksi().stream()
                .filter(t -> t.getIdTransaksi() == id)
                .findFirst();

        if (transaksiOpt.isEmpty()) {
            return "redirect:/admin/kelolatransaksi"; // Redirect jika transaksi tidak ditemukan
        }

        Transaksi transaksi = transaksiOpt.get();
        model.addAttribute("transaksi", transaksi);
        model.addAttribute("transaksiFilms", transaksiFilms);
        return "admin/detailtransaksi";
    }
}