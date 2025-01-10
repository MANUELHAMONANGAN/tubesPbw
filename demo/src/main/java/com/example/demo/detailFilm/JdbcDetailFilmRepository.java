package com.example.demo.detailFilm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcDetailFilmRepository implements DetailFilmRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> findAllGenres() {
        String sql = "select * from genre order by nama";
        return this.jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public List<DetailFilm> findFilmById(int id) {
        String sql = """
                select *
                from
                    (select tabelGenreAktor.idfilm, tabelGenreAktor.judul, tabelGenreAktor.stock, tabelGenreAktor.coverfilm, tabelGenreAktor.hargaperhari, tabelGenreAktor.deskripsi, tabelGenreAktor.durasi, array_agg(tabelGenreAktor.genres order by tabelGenreAktor.genres) as "genres", tabelGenreAktor.actors
                    from
                        (select film.idfilm, film.judul, film.stock, film.coverfilm, film.hargaperhari, film.deskripsi, film.durasi, genre.nama as "genres", array_agg(aktor.idaktor order by aktor.nama) as "actors"
                        from
                            film
                            inner join filmaktor on film.idfilm = filmaktor.idfilm
                            inner join filmgenre on film.idfilm = filmgenre.idfilm
                            inner join genre on filmgenre.idgenre = genre.idgenre
                            inner join aktor on filmaktor.idaktor = aktor.idaktor
                        group by film.idfilm, film.judul, genre.nama) as tabelGenreAktor
                    group by tabelGenreAktor.idfilm, tabelGenreAktor.judul, tabelGenreAktor.stock, tabelGenreAktor.coverfilm, tabelGenreAktor.hargaperhari, tabelGenreAktor.deskripsi, tabelGenreAktor.durasi, tabelGenreAktor.actors
                    order by tabelGenreAktor.judul) as tabelFinal
                where tabelFinal.idfilm = ?;
                """;
            return this.jdbcTemplate.query(sql, this::mapRowToDetailFilm, id);
    }

    @Override
    public List<Aktor> findActorsById(List<Integer> id) {
        String sql = "select * from aktor where ";
        for(int i = 0; i < id.size(); i++) {
            if(i == 0) {
                sql = sql + "idaktor = " + id.get(i);
            }else {
                sql = sql + " or idaktor = " + id.get(i);
            }
        }

        return this.jdbcTemplate.query(sql, this::mapRowToAktor);
    }

    private DetailFilm mapRowToDetailFilm(ResultSet rs, int rowNum) throws SQLException {
        return new DetailFilm(rs.getInt("idfilm"),
                            rs.getString("judul"), 
                            rs.getInt("stock"), 
                            Base64.getEncoder().encodeToString(rs.getBytes("coverfilm")), 
                            rs.getInt("hargaperhari"), 
                            rs.getString("deskripsi"), 
                            rs.getInt("durasi"), 
                            Arrays.asList((String[]) rs.getArray("genres").getArray()),
                            Arrays.asList((Integer[]) rs.getArray("actors").getArray()));
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
            rs.getInt("idgenre"),
            rs.getString("nama")
        );
    }

    private Aktor mapRowToAktor(ResultSet rs, int rowNum) throws SQLException {
        return new Aktor(rs.getInt("idaktor"),
                         rs.getString("nama"),
                         rs.getString("tanggallahir"),
                         rs.getString("deskripsidiri"),
                         Base64.getEncoder().encodeToString(rs.getBytes("fotoprofil")));
    }
}
