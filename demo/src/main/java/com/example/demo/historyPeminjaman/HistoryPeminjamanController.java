package com.example.demo.historyPeminjaman;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class HistoryPeminjamanController {
    @Autowired
    private HistoryPeminjamanRepository repository;

    @GetMapping("/history")
    public String historyPeminjamanView(Model model, HttpSession session) {
        if(session.getAttribute("idUser") == null) {
            model.addAttribute("logOutDisable", "true");
            model.addAttribute("cartDisable", "true");
        }else {
            model.addAttribute("logOutDisable", "false");
            model.addAttribute("cartDisable", "false");
        }

        model.addAttribute("filterDisable", "true");

        List<HistoryPeminjaman> historyPeminjaman = this.repository.findAllTransaksiDone((int)session.getAttribute("idUser"));
        if(historyPeminjaman.size() == 0) {
            model.addAttribute("historyPinjam", "false");
            model.addAttribute("showTabel", "false");
        }else {
            model.addAttribute("showTabel", "true");
        }

        model.addAttribute("historyPeminjaman", historyPeminjaman);

        return "historyPeminjaman/index";
    }

    @PostMapping("/history/filter")
    public String FilteredHistoryView(
        Model model, 
        HttpSession session, 
        @RequestParam("tanggalAwal") String tanggalAwal, 
        @RequestParam("tanggalAkhir") String tanggalAkhir) 
    {
        if(session.getAttribute("idUser") == null) {
            model.addAttribute("logOutDisable", "true");
            model.addAttribute("cartDisable", "true");
        }else {
            model.addAttribute("logOutDisable", "false");
            model.addAttribute("cartDisable", "false");
        }

        model.addAttribute("filterDisable", "true");

        if(tanggalAkhir.compareTo(tanggalAwal) < 0 || tanggalAkhir.equals("") || tanggalAwal.equals("")) {
            model.addAttribute("inputSalah", "true");
            model.addAttribute("showTabel", "false");
            return "historyPeminjaman/index";
        }else {
            List<HistoryPeminjaman> historyPeminjaman = this.repository.findFilteredTransaksi((int)session.getAttribute("idUser"), tanggalAwal, tanggalAkhir);
            if(historyPeminjaman.size() == 0) {
                model.addAttribute("filterHistory", "false");
                model.addAttribute("showTabel", "false");
            }else {
                model.addAttribute("showTabel", "true");
            }

            model.addAttribute("historyPeminjaman", historyPeminjaman);
            return "historyPeminjaman/index";
        }
    }
}
