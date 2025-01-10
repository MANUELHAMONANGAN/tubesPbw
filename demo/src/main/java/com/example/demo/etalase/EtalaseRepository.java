package com.example.demo.etalase;

import java.util.List;

public interface EtalaseRepository {
    List<Film> findAllFilm();
    List<Genre> findAllGenre();
    // List<Film> findLatestFilm();
}
