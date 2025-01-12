package com.example.demo.etalase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.example.demo.aspect.RequiredRole;
import jakarta.servlet.http.HttpSession;

@Controller
public class EtalaseController {
    @Autowired
    private EtalaseRepository repository;

    @GetMapping
    public String etalaseView(Model model, HttpSession session, @RequestParam(required = false) String page) {
        if(session.getAttribute("idUser") == null) {
            model.addAttribute("logOutDisable", "true");
            model.addAttribute("cartDisable", "true");
        }else {
            model.addAttribute("logOutDisable", "false");
            model.addAttribute("cartDisable", "false");
        }

        model.addAttribute("filterDisable", "false");

        List<Film> films = this.repository.findAllFilm();

        int jmlPage = (int) Math.ceil(((films.size() * 1.0) / 15.0));
        model.addAttribute("pageCount", jmlPage);
        
        if(films.size() == 0) {
            model.addAttribute("status", "none");
        }else {
            List<Film> filmsPerPage = new ArrayList<>();
            if(page == null) {
                int jml = 0;
                for(int i = 0; i < films.size() && jml < 15; i++) {
                    filmsPerPage.addLast(films.get(i));
                    jml++;
                }
                model.addAttribute("currentPage", 1);
            }else {
                int currPage = Integer.parseInt(page);
                int startIdx = (currPage - 1) * 15;
                int jml = 0;
                for(int i = startIdx; i < films.size() && jml < 15; i++) {
                    filmsPerPage.addLast(films.get(i));
                    jml++;
                }
                model.addAttribute("currentPage", page);
            }

            model.addAttribute("films", filmsPerPage);
        }

        List<Genre> listGenre = this.repository.findAllGenre();
        model.addAttribute("genres", listGenre);

        List<Film> slideShowFilms = this.repository.findLatestFilm();
        int jmlSlideShow = (int)Math.ceil(slideShowFilms.size() / 5);
        model.addAttribute("slideFilms", slideShowFilms);
        model.addAttribute("jmlSlide", jmlSlideShow);
        
        return "/etalase/index";
    }

    @PostMapping
    public String EtalaseFilterView(Model model, 
        HttpSession session,
        @RequestParam(required = false) String page,
        @RequestParam(required = false) List<String> genre,
        @RequestParam(required = false) String aktor1,
        @RequestParam(required = false) String aktor2,
        @RequestParam(required = false) String judulfilm) 
    {
        if(session.getAttribute("idUser") == null) {
            model.addAttribute("logOutDisable", "true");
            model.addAttribute("cartDisable", "true");
        }else {
            model.addAttribute("logOutDisable", "false");
            model.addAttribute("cartDisable", "false");
        }

        model.addAttribute("filterDisable", "false");
        
        List<Film> films = this.repository.findAllFilm();

        if(genre != null) {
            int i = 0;
            while(i < films.size()) {
                Film currFilm = films.get(i);
                List<String> currGenres = currFilm.getGenres();
                boolean masuk = true;

                for(int j = 0; j < genre.size(); j++) {
                    if(!currGenres.contains(genre.get(j))) {
                        masuk = false;
                        break;
                    }
                }
                
                if(!masuk) {
                    films.remove(currFilm);
                    i = 0;
                }else {
                    i++;
                }
            }
        }

        if(aktor1 != null && !aktor1.equals("")) {
            int i = 0;
            while(i < films.size()) {
                Film currFilm = films.get(i);
                List<String> actors = currFilm.getActors();
                boolean masuk = false;
                
                for(int j = 0; j < actors.size(); j++) {
                    if(actors.get(j).toLowerCase().contains(aktor1.toLowerCase())) {
                        masuk = true;
                        break;
                    }
                }

                if(!masuk) {
                    films.remove(currFilm);
                    i = 0;
                }else {
                    i++;
                }
            }
        }

        if(aktor2 != null && !aktor2.equals("")) {
            int i = 0;
            while(i < films.size()) {
                Film currFilm = films.get(i);
                List<String> actors = currFilm.getActors();
                boolean masuk = false;
                
                for(int j = 0; j < actors.size(); j++) {
                    if(actors.get(j).toLowerCase().contains(aktor2.toLowerCase())) {
                        masuk = true;
                        break;
                    }
                }

                if(!masuk) {
                    films.remove(currFilm);
                    i = 0;
                }else {
                    i++;
                }
            }
        }

        if(judulfilm != null && !judulfilm.equals("")) {
            int i = 0;
            while(i < films.size()) {
                Film currFilm = films.get(i);
                boolean masuk = false;
                
                if(currFilm.getJudul().toLowerCase().contains(judulfilm)) {
                    masuk = true;
                }

                if(!masuk) {
                    films.remove(currFilm);
                    i = 0;
                }else {
                    i++;
                }
            }
        }

        int jmlPage = (int) Math.ceil(((films.size() * 1.0) / 15.0));
        model.addAttribute("pageCount", jmlPage);
        
        if(films.size() == 0) {
            model.addAttribute("status", "none");
        }else {
            List<Film> filmsPerPage = new ArrayList<>();
            if(page == null) {
                int jml = 0;
                for(int i = 0; i < films.size() && jml < 15; i++) {
                    filmsPerPage.addLast(films.get(i));
                    jml++;
                }
                model.addAttribute("currentPage", 1);
            }else {
                int currPage = Integer.parseInt(page);
                int startIdx = (currPage - 1) * 15;
                int jml = 0;
                for(int i = startIdx; i < films.size() && jml < 15; i++) {
                    filmsPerPage.addLast(films.get(i));
                    jml++;
                }
                model.addAttribute("currentPage", page);
            }

            model.addAttribute("films", filmsPerPage);
        }

        List<Genre> listGenre = this.repository.findAllGenre();
        model.addAttribute("genres", listGenre);

        List<Film> slideShowFilms = this.repository.findLatestFilm();
        int jmlSlideShow = (int)Math.ceil(slideShowFilms.size() / 5);
        model.addAttribute("slideFilms", slideShowFilms);
        model.addAttribute("jmlSlide", jmlSlideShow);
        
        return "/etalase/index";
    }
}