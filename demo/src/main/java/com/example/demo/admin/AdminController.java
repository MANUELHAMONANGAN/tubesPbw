package com.example.demo.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.laporan.LaporanService;
import com.example.demo.laporan.TopFilm;
import com.example.demo.laporan.TopGenre;

@Controller
public class AdminController {
    @Autowired
    private LaporanService laporanService;
    
    @GetMapping("/admin/")
    public String index(Model model){
        model.addAttribute("pageSaatIni","home");

        //HOME ISINYA LAPORAN BULAN INI
        //WEEKLY SALES

        //JUMLAH FILM DISEWA
        model.addAttribute("jumlahPenyewaan", this.laporanService.getFilmDisewaThisMonth().get().getJumlahPenyewaan());

        //TOP GENRE
        TopGenre topGenre = this.laporanService.getTopGenreThisMonth();
        if(topGenre == null){
            model.addAttribute("topGenre", "Belum ada Penyewaan");
        }else{
            model.addAttribute("topGenre", topGenre.getNamaGenre());
        }

        //GRAPH
        model.addAttribute("graphTitle", "Bulan Ini");
        model.addAttribute("graphData", this.laporanService.getGraphDataThisMonth());

        //TOP 5 FILM PALING LAKU (PALING BANYAK DIPESAN)
        model.addAttribute("top5BestFilm", this.laporanService.getTop5BestFilmThisMonth());

        //TOP 5 FILM PALING GA LAKU (PALING SEDIKIT PENYEWAAN)
        model.addAttribute("top5WorstFilm", this.laporanService.getTop5WorstFilmThisMonth());

        //LIST OUT OF STOCK
        model.addAttribute("listOutOfStock", this.laporanService.getListOutOfStockThisMonth());

        //TOP 5 GENRE PALING LAKU (PALING BANYAK DIPESAN)
        model.addAttribute("top5BestGenre", this.laporanService.getTop5GenreThisMonth());
        return "/admin/dashboard";
    }

    @GetMapping("/genre/")
    public String genre(Model model){
        model.addAttribute("pageSaatIni","genre");
        return "/admin/genre";
    }
    
    @GetMapping("/aktor/")
    public String aktor(Model model, @RequestParam( defaultValue = "",required = false) String filter,
     @RequestParam(defaultValue = "1", required = false) Integer page){

        int pageCount = 8;
        model.addAttribute("pageSaatIni","aktor");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "/admin/aktor";
    }

    @GetMapping("/graph/")
    public String graph(Model model){
        model.addAttribute("graphTitle", "Bulan November");
        model.addAttribute("graphData", this.laporanService.getGraphDataThisMonth());
        return "/graph/graph";
    }
}