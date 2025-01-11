package com.example.demo.admin;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAdminRepository implements AdminRepository {
    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public List<Aktor> findAllAktor(int max, int currentPage) {
        return jdbc.query(
            "SELECT idaktor, nama, tanggallahir, deskripsidiri, fotoprofil FROM aktor ORDER BY nama LIMIT ? OFFSET ? ",
            this::mapRowToAktor, max, currentPage * max - max);
    }

    @Override
    public List<Film> findAllFilm(int max, int currentPage) {
        return jdbc.query("SELECT idfilm, judul, stock, coverfilm, hargaperhari, deskripsi, durasi, tahunrilis, averagerating FROM Film ORDER BY judul LIMIT ? OFFSET ?", this::mapRowToFilm, max, currentPage * max - max);
    }

    @Override
    public List<Film> findFilmById(int idFilm) {
        return jdbc.query("SELECT idfilm, judul, stock, coverfilm, hargaperhari, deskripsi, durasi, tahunrilis, averagerating FROM Film WHERE idfilm = ?", this::mapRowToFilm, idFilm);
    }

    @Override
    public List<Film> findFilmByName(String name, int maxPage, int currentPage) {
        return jdbc.query(
            "SELECT idfilm, judul, stock, coverfilm, hargaperhari, deskripsi, durasi, tahunrilis, averagerating FROM Film where judul ILIKE ? ORDER BY judul LIMIT ? OFFSET ?",
            this::mapRowToFilm,"%" + name + "%", maxPage, currentPage * maxPage - maxPage);  
    }

    @Override
    public List<Aktor> findAktorByName(String name, int maxPage, int currentPage) {
        return jdbc.query(
            "SELECT idaktor, nama, tanggallahir, deskripsidiri, fotoprofil FROM aktor where nama ILIKE ? ORDER BY nama LIMIT ? OFFSET ?",
            this::mapRowToAktor,"%" + name + "%", maxPage, currentPage * maxPage - maxPage);   
    }

    @Override
    public List<Aktor> findAktorById(int idAktor) {
        return jdbc.query(
            "SELECT idaktor, nama, tanggallahir, deskripsidiri, fotoprofil FROM aktor where idAktor = ?",
            this::mapRowToAktor,idAktor);   
    }

    @Override
    public void update(int idAktor, String nama, Date tanggallahir, String deskripsiDiri) {
        jdbc.update("UPDATE aktor SET  nama = ?, tanggallahir = ?, deskripsidiri = ? WHERE idAktor = ?", nama, tanggallahir, deskripsiDiri, idAktor);
    }


    @Override
    public void updateGambar(int idAktor, byte[] imagePath){
        jdbc.update("UPDATE aktor SET fotoprofil = ? WHERE idAktor = ?", imagePath, idAktor);
    }

    @Override
    public void addAktor(String nama, Date tanggallahir, String deskripsiDiri, byte[] imagePath) {
        jdbc.update("INSERT INTO aktor (nama, tanggallahir, deskripsidiri, fotoprofil) VALUES (?, ?, ?, ?)", nama, tanggallahir, deskripsiDiri, imagePath);        
    }

    @Override
    public List<Genre> findAllGenre() {
        return jdbc.query("SELECT nama FROM genre ORDER BY nama", this::mapRowToGenre);
    }

    @Override
    public void addGenre(String genre_name) {
        jdbc.update("INSERT INTO genre (nama) VALUES (?)", genre_name);
    }

    public int getCountAktor() {
        Iterable<Aktor> user =  jdbc.query(
        "SELECT idaktor, nama, tanggallahir, deskripsidiri, fotoprofil FROM aktor",
        this::mapRowToAktor);

        int count = 0;
        for (Aktor u : user) {
            count++;
        }
        return count;
    }

    public int getCountAktorFilter(String name) {
        Iterable<Aktor> user =  jdbc.query(
        "SELECT idaktor, nama, tanggallahir, deskripsidiri, fotoprofil FROM aktor where nama ILIKE ?",
        this::mapRowToAktor, "%" + name + "%");

        int count = 0;
        for (Aktor u : user) {
            count++;
        }
        return count;
    }

    public int getCountFilm() {
        Iterable<Film> user =  jdbc.query(
        "SELECT idfilm, judul, stock, coverfilm, hargaperhari, deskripsi, durasi, tahunrilis, averagerating FROM Film",
        this::mapRowToFilm);

        int count = 0;
        for (Film u : user) {
            count++;
        }
        return count;
    }

    public int getCountFilmFilter(String name) {
        Iterable<Film> user =  jdbc.query(
        "SELECT idfilm, judul, stock, coverfilm, hargaperhari, deskripsi, durasi, tahunrilis, averagerating FROM Film where judul ILIKE ?",
        this::mapRowToFilm, "%" + name + "%");

        int count = 0;
        for (Film u : user) {
            count++;
        }
        return count;
    }

    private Aktor mapRowToAktor(ResultSet resultSet, int rowNum) throws SQLException{
        String idAktor = ""+ resultSet.getInt("idaktor");
        return new Aktor(
            idAktor,
            Base64.getEncoder().encodeToString(resultSet.getBytes("fotoprofil")),
            resultSet.getString("nama"),
            resultSet.getDate("tanggallahir"),
            resultSet.getString("deskripsidiri")
        );
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException{
        return new Genre(
            rs.getString("nama")
        );
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException{
        return new Film(
            rs.getString("idfilm"),
            rs.getString("judul"),
            rs.getString("stock"),
            Base64.getEncoder().encodeToString(rs.getBytes("coverfilm")),
            rs.getString("hargaperhari"),
            rs.getString("deskripsi"),
            rs.getString("durasi"),
            rs.getString("tahunrilis"),
            rs.getString("averagerating")
        );
    }
}
