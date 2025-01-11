package com.example.demo.admin;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class AdminController {
    private static final int maxPage = 5;

    @Autowired
    private AdminRepository repo;

    @GetMapping("/admin/")
    public String index(Model model){
        model.addAttribute("pageSaatIni","home");
        return "/admin/dashboard";
    }

    @GetMapping("/genre/")
    public String genre(Model model){
        List<Genre> listGenre = this.repo.findAllGenre();

        model.addAttribute( "genreList", listGenre);
        if(!listGenre.isEmpty()){
            model.addAttribute("genre_available", true);
        } 
        model.addAttribute("pageSaatIni","genre");
        return "/admin/genre";
    }
    
    @PostMapping("/genre/")
    public String addGenre(Model model, @RequestParam String genre_name){
        this.repo.addGenre(genre_name);
        return "redirect:/genre/";
    }

    @GetMapping("/aktor/")
    public String aktor(Model model, @RequestParam( defaultValue = "",required = false) String filter,
     @RequestParam(defaultValue = "1", required = false) Integer page){

        List<Aktor> user;
        int pageCount;

        if (filter != null && !filter.isEmpty()){
            user = this.repo.findAktorByName(filter, maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountAktorFilter(filter)/maxPage); 
        } else {
            user = this.repo.findAllAktor(maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountAktor()/maxPage); 

        }

        model.addAttribute("Aktor", user);
        model.addAttribute("pageSaatIni","aktor");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "/admin/aktor";
    }

    @GetMapping("/aktor/edit/")
    public String editAktor(Model model, @RequestParam int idAktor){
        List<Aktor> user = this.repo.findAktorById(idAktor);
        model.addAttribute("Aktor", user.getFirst());
        return "admin/editAktor";
    }

    @PostMapping("/aktor/edit/")
    public String updateAktor(Model model, @RequestParam int idAktor, @RequestParam String nama , @RequestParam Date tanggal_lahir, @RequestParam String deskripsi_diri, @RequestParam MultipartFile foto) throws Exception{
        repo.update(idAktor, nama, tanggal_lahir, deskripsi_diri);
        
        if(foto != null && !foto.isEmpty()){
            byte[] imageBytes =  foto.getBytes();
            repo.updateGambar(idAktor, imageBytes);
        }
        // return "redirect:/aktor/edit/?idAktor=" +idAktor;
        return "redirect:/aktor/";
    }

    @GetMapping("/aktor/tambah/")
    public String addAktor(Model model){
        return "admin/addAktor";
    }

    @PostMapping("/aktor/tambah/")
    public String postAddAktor(Model model, @RequestParam String nama, @RequestParam int tanggal_lahir, @RequestParam int bulan_lahir, @RequestParam int tahun_lahir, @RequestParam String deskripsi_diri, @RequestParam MultipartFile foto) throws Exception{
        LocalDate localDate = LocalDate.of(tahun_lahir, bulan_lahir, tanggal_lahir);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        repo.addAktor(nama, sqlDate, deskripsi_diri, foto.getBytes());
        return "redirect:/aktor/";
    }

    @GetMapping("/aktor/koleksi_film/")
    public String koleksi_film(Model model, @RequestParam( defaultValue = "",required = false) String filter,
    @RequestParam(defaultValue = "1", required = false) Integer page){
        List<Film> film;
        int pageCount;

        if (filter != null && !filter.isEmpty()){
            film = this.repo.findFilmByName(filter, maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountFilmFilter(filter)/maxPage); 
        } else {
            film = this.repo.findAllFilm(maxPage, page);
            pageCount = (int) Math.ceil((double) this.repo.getCountFilm()/maxPage); 

        }

        model.addAttribute("listFilm", film);
        model.addAttribute("pageSaatIni","koleksi_film");
        model.addAttribute("filter", filter);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageCount", pageCount);
        return "admin/listfilm";
    }
    
}