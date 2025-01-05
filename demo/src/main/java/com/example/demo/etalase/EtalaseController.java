package com.example.demo.etalase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EtalaseController {
    @Autowired
    private EtalaseRepository repository;

    private boolean cekGenre(Film film, List<String> genres) {
        boolean masuk = true;
        for(int j = 0; j < genres.size(); j++) {
            if(!film.getGenres().contains(genres.get(j))) {
                masuk = false;
                break;
            }
        }

        return masuk;
    }

    @GetMapping
    public String etalaseView(
        Model model, 
        @RequestParam(required = false) String page,
        @RequestParam(required = false) List<String> genre,
        @RequestParam(required = false) String aktor1,
        @RequestParam(required = false) String aktor2,
        @RequestParam(required = false) String judulfilm
    ) {
        List<Film> films = this.repository.findAllFilm();
        List<Film> filteredFilms = new ArrayList<>();

        if(genre == null && aktor1 == null && aktor2 == null && judulfilm == null) { //ga filter
            for(int i = 0; i < films.size(); i++) {
                filteredFilms.addLast(films.get(i));
            }
        }else if(genre == null && aktor1.equals("") && aktor2.equals("") && judulfilm.equals("")) { //ga filter
            return "redirect:/";
        }else if(genre != null && aktor1.equals("") && aktor2.equals("") && judulfilm.equals("")) { //filter genre aja
            for(int i = 0; i < films.size(); i++) {
                boolean masuk = cekGenre(films.get(i), genre);
                if(masuk) {
                    filteredFilms.addLast(films.get(i));
                }
            }
        }else if(genre == null && !aktor1.equals("") && aktor2.equals("") && judulfilm.equals("")) { //filter aktor1 aja
            for(int i = 0; i < films.size(); i++) {
                if(films.get(i).getActors().contains(aktor1)) {
                    filteredFilms.addLast(films.get(i));
                }
            }
        }else if(genre == null && aktor1.equals("") && !aktor2.equals("") && judulfilm.equals("")) { //filter aktor2 aja
            for(int i = 0; i < films.size(); i++) {
                if(films.get(i).getActors().contains(aktor2)) {
                    filteredFilms.addLast(films.get(i));
                }
            }
        }else if(genre == null && aktor1.equals("") && aktor2.equals("") && !judulfilm.equals("")) {  //filter judul aja
            for(int i = 0; i < films.size(); i++) {
                if(films.get(i).getJudul().toLowerCase().contains(judulfilm.toLowerCase())) {
                    filteredFilms.addLast(films.get(i));
                }
            }
        }else if(genre != null && !aktor1.equals("") && aktor2.equals("") && judulfilm.equals("")) { //filter genre & aktor1
            for(int i = 0; i < films.size(); i++) {
                boolean masuk = cekGenre(films.get(i), genre);
                if(masuk) {
                    if(films.get(i).getActors().contains(aktor1)) {
                        filteredFilms.addLast(films.get(i));
                    }
                }
            }
        }else if(genre != null && aktor1.equals("") && !aktor2.equals("") && judulfilm.equals("")) {  //filter genre & aktor2
            for(int i = 0; i < films.size(); i++) {
                boolean masuk = cekGenre(films.get(i), genre);
                if(masuk) {
                    if(films.get(i).getActors().contains(aktor2)) {
                        filteredFilms.addLast(films.get(i));
                    }
                }
            }
        }else if(genre != null && aktor1.equals("") && aktor2.equals("") && !judulfilm.equals("")) {  //filter genre & judul
            for(int i = 0; i < films.size(); i++) {
                boolean masuk = cekGenre(films.get(i), genre);
                if(masuk) {
                    if(films.get(i).getJudul().toLowerCase().contains(judulfilm.toLowerCase())) {
                        filteredFilms.addLast(films.get(i));
                    }
                }
            }
        }else if(genre == null && !aktor1.equals("") && !aktor2.equals("") && judulfilm.equals("")) {  //filter aktor1 & 2
            for(int i = 0; i < films.size(); i++) {
                if(films.get(i).getActors().contains(aktor1) && films.get(i).getActors().contains(aktor2)) {
                    filteredFilms.add(films.get(i));
                }
            }
        }else if(genre == null && !aktor1.equals("") && aktor2.equals("") && !judulfilm.equals("")) {  //filter aktor1 & judul
            for(int i = 0; i < films.size(); i++) {
                if(films.get(i).getActors().contains(aktor1) && films.get(i).getJudul().toLowerCase().contains(judulfilm.toLowerCase())) {
                    filteredFilms.add(films.get(i));
                }
            }
        }else if(genre == null && aktor1.equals("") && !aktor2.equals("") && !judulfilm.equals("")) {  //filter aktor2 & judul
            for(int i = 0; i < films.size(); i++) {
                if(films.get(i).getActors().contains(aktor2) && films.get(i).getJudul().toLowerCase().contains(judulfilm.toLowerCase())) {
                    filteredFilms.add(films.get(i));
                }
            }
        }else if(genre != null && !aktor1.equals("") && !aktor2.equals("") && judulfilm.equals("")) {  //filter genre & aktor1 & aktor2
            for(int i = 0; i < films.size(); i++) {
                boolean masuk = cekGenre(films.get(i), genre);
                if(masuk) {
                    if(films.get(i).getActors().contains(aktor1) && films.get(i).getActors().contains(aktor2)) {
                        filteredFilms.add(films.get(i));
                    }
                }
            }
        }else if(genre == null && !aktor1.equals("") && !aktor2.equals("") && !judulfilm.equals("")) {  //filter aktor1 & aktor2 & judul
            for(int i = 0; i < films.size(); i++) {
                if(films.get(i).getActors().contains(aktor1) && films.get(i).getActors().contains(aktor2) && films.get(i).getJudul().toLowerCase().contains(judulfilm.toLowerCase())) {
                    filteredFilms.add(films.get(i));
                }
            }
        }else if(genre != null && aktor1.equals("") && !aktor2.equals("") && !judulfilm.equals("")) {  //filter aktor2 & judul & genre
            for(int i = 0; i < films.size(); i++) {
                boolean masuk = cekGenre(films.get(i), genre);
                if(masuk) {
                    if(films.get(i).getJudul().toLowerCase().contains(judulfilm.toLowerCase()) && films.get(i).getActors().contains(aktor2)) {
                        filteredFilms.addLast(films.get(i));
                    }
                }
            }
        }else if(genre != null && !aktor1.equals("") && aktor2.equals("") && !judulfilm.equals("")) {  //filter judul & genre & aktor1
            for(int i = 0; i < films.size(); i++) {
                boolean masuk = cekGenre(films.get(i), genre);
                if(masuk) {
                    if(films.get(i).getJudul().toLowerCase().contains(judulfilm.toLowerCase()) && films.get(i).getActors().contains(aktor1)) {
                        filteredFilms.addLast(films.get(i));
                    }
                }
            }
        }else { //filter semua
            for(int i = 0; i < films.size(); i++) {
                boolean masuk = cekGenre(films.get(i), genre);
                if(masuk) {
                    if(films.get(i).getJudul().toLowerCase().contains(judulfilm.toLowerCase()) && films.get(i).getActors().contains(aktor1) && films.get(i).getActors().contains(aktor2)) {
                        filteredFilms.addLast(films.get(i));
                    }
                }
            }
        }

        int jmlPage = (int) Math.ceil(((filteredFilms.size() * 1.0) / 15.0));
        model.addAttribute("pageCount", jmlPage);
        
        if(filteredFilms.size() == 0) {
            model.addAttribute("status", "none");
        }else {
            List<Film> filmsPerPage = new ArrayList<>();
            if(page == null) {
                int jml = 0;
                for(int i = 0; i < filteredFilms.size() && jml < 15; i++) {
                    filmsPerPage.addLast(filteredFilms.get(i));
                    jml++;
                }
                model.addAttribute("currentPage", 1);
            }else {
                int currPage = Integer.parseInt(page);
                int startIdx = (currPage - 1) * 15;
                int jml = 0;
                for(int i = startIdx; i < filteredFilms.size() && jml < 15; i++) {
                    filmsPerPage.addLast(films.get(i));
                    jml++;
                }
                model.addAttribute("currentPage", page);
            }

            model.addAttribute("films", filmsPerPage);
        }

        List<Genre> listGenre = this.repository.findAllGenre();
        model.addAttribute("genres", listGenre);
        model.addAttribute("genreFil", genre);
        
        return "/etalase/index";
    }
}
