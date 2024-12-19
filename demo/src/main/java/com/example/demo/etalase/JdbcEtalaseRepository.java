package com.example.demo.etalase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcEtalaseRepository implements EtalaseRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAllFilm() {
        String sql = "select * from film";
        return this.jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
            rs.getInt("idfilm"),
            rs.getString("judul"),
            rs.getInt("stock"),
            Base64.getEncoder().encodeToString(rs.getBytes("coverfilm")),
            rs.getInt("hargaperhari"),
            rs.getString("deskripsi"),
            rs.getInt("durasi")
        );
    }
}
