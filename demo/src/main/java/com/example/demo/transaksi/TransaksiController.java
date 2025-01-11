package com.example.demo.transaksi;

import java.util.List;

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

    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable int id, Model model) {
        List<TransaksiFilm> transaksiFilms = transaksiService.getTransaksiFilmsByTransaksiId(id);
        model.addAttribute("transaksiFilms", transaksiFilms);
        return "admin/detailtransaksi";
    }
}
