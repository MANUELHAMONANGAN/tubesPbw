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
    public List<Aktor> findAktorByName(String name, int maxPage, int currentPage) {
        return jdbc.query(
            "SELECT idaktor, nama, tanggallahir, deskripsidiri, fotoprofil FROM aktor where nama ILIKE ? ORDER BY nama  LIMIT ? OFFSET ?",
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
    private Aktor mapRowToAktor(ResultSet resultSet, int rowNum) throws SQLException{
        String idAktor = ""+ resultSet.getInt("idaktor");

        return new Aktor(
            idAktor,
            Base64.getEncoder().encodeToString(resultSet.getBytes("fotoprofil")),
            resultSet.getString("nama"),
            // formatted_date,
            resultSet.getDate("tanggallahir"),
            resultSet.getString("deskripsidiri")
        );
    }

    public int getCount() {
        Iterable<Aktor> user =  jdbc.query(
        "SELECT idaktor, nama, tanggallahir, deskripsidiri, fotoprofil FROM aktor",
        this::mapRowToAktor);

        int count = 0;
        for (Aktor u : user) {
            count++;
        }
        return count;
    }

    public int getCountFilter(String name) {
        Iterable<Aktor> user =  jdbc.query(
        "SELECT idaktor, nama, tanggallahir, deskripsidiri, fotoprofil FROM aktor where nama ILIKE ?",
        this::mapRowToAktor, "%" + name + "%");

        int count = 0;
        for (Aktor u : user) {
            count++;
        }
        return count;
    }
}
