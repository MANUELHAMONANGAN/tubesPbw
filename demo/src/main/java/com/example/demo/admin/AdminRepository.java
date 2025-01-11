package com.example.demo.admin;

import java.sql.Date;
import java.util.List;

public interface AdminRepository {
    List<Aktor> findAllAktor(int maxPage, int currentPage);
    List<Aktor> findAktorByName(String name, int maxPage, int currentPage);
    List<Aktor> findAktorById(int idAktor);
    int getCountAktor();
    int getCountAktorFilter(String name);
    void update(int idAktor, String nama, Date tanggallahir, String deskripsiDiri);
    void updateGambar(int idAktor, byte[] imagePath);

    void addAktor(String nama, Date tanggallahir, String deskripsiDiri, byte[] imagePath);

    List<Film> findAllFilm(int maxPage, int currentPage);
    List<Film> findFilmByName(String name, int maxPage, int currentPage);
    List<Film> findFilmById(int idFilm);

    int getCountFilm();
    int getCountFilmFilter(String name);

    List<Genre> findAllGenre();
    void addGenre(String genre_name);
}
