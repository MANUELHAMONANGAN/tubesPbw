package com.example.demo.detailFilm;

import java.util.List;

public interface DetailFilmRepository {
    List<Genre> findAllGenres();
    List<DetailFilm> findFilmById(int id);
    List<Aktor> findActorsById(List<Integer> id);
}
