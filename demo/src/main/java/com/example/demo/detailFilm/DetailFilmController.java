package com.example.demo.detailFilm;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class DetailFilmController {
    @Autowired
    private DetailFilmRepository repository;    

    @GetMapping("/film/{judul}/{id}")
    public String detailFilmView(HttpSession session, @PathVariable String judul, @PathVariable int id, Model model) {
        if(session.getAttribute("idUser") == null) {
            model.addAttribute("logOutDisable", "true");
            model.addAttribute("cartDisable", "true");
        }else {
            model.addAttribute("logOutDisable", "false");
            model.addAttribute("cartDisable", "false");
        }
        
        model.addAttribute("filterDisable", "false");
        
        List<Genre> genres = this.repository.findAllGenres();
        model.addAttribute("genres", genres);
        
        List<DetailFilm> film = this.repository.findFilmById(id);
        model.addAttribute("filmDetail", film.getFirst());

        List<Aktor> actors = this.repository.findActorsById(film.getFirst().getIdAktor());
        model.addAttribute("actors", actors);
        
        return "/detailFilm/index";
    }
}
