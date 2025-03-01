package com.example.demo.etalase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
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
        String sql = """
                    select tabelGenreAktor.idfilm, tabelGenreAktor.judul, tabelGenreAktor.coverfilm, array_agg(tabelGenreAktor.genres order by tabelGenreAktor.genres) as "genres", tabelGenreAktor.actors, tabelGenreAktor.tahunrilis, tabelGenreAktor.averagerating
                    from
                        (select film.idfilm, film.judul, film.coverfilm, genre.nama as "genres", array_agg(aktor.nama order by aktor.nama) as "actors", film.tahunrilis, film.averagerating
                        from
                            film
                            inner join filmaktor on film.idfilm = filmaktor.idfilm
                            inner join filmgenre on film.idfilm = filmgenre.idfilm
                            inner join genre on filmgenre.idgenre = genre.idgenre
                            inner join aktor on filmaktor.idaktor = aktor.idaktor
                        group by film.idfilm, film.judul, film.coverfilm, genre.nama, film.tahunrilis, film.averagerating) as tabelGenreAktor
                    group by tabelGenreAktor.idfilm, tabelGenreAktor.judul, tabelGenreAktor.coverfilm, tabelGenreAktor.actors, tabelGenreAktor.tahunrilis, tabelGenreAktor.averagerating
                    order by tabelGenreAktor.judul
                """;
        return this.jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public List<Film> findLatestFilm() {
        String sql = """
                select tabelGenreAktor.idfilm, tabelGenreAktor.judul, tabelGenreAktor.coverfilm, tabelGenreAktor.tahunrilis, tabelGenreAktor.averagerating, array_agg(tabelGenreAktor.genres order by tabelGenreAktor.genres) as "genres", tabelGenreAktor.actors
                from
                    (select film.idfilm, film.judul, film.coverfilm, film.tahunrilis, film.averagerating, genre.nama as "genres", array_agg(aktor.nama order by aktor.nama) as "actors"
                    from
                        film
                        inner join filmaktor on film.idfilm = filmaktor.idfilm
                        inner join filmgenre on film.idfilm = filmgenre.idfilm
                        inner join genre on filmgenre.idgenre = genre.idgenre
                        inner join aktor on filmaktor.idaktor = aktor.idaktor
                    group by film.idfilm, film.judul, film.coverfilm, film.tahunrilis, film.averagerating, genre.nama) as tabelGenreAktor
                group by tabelGenreAktor.idfilm, tabelGenreAktor.judul, tabelGenreAktor.coverfilm, tabelGenreAktor.actors, tabelGenreAktor.tahunrilis, tabelGenreAktor.averagerating
                order by tabelGenreAktor.tahunrilis desc
                limit 10
                """;
        return this.jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public List<Genre> findAllGenre() {
        String sql = "select * from genre order by nama";
        return this.jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
            rs.getInt("idfilm"),
            rs.getString("judul"),
            Base64.getEncoder().encodeToString(rs.getBytes("coverfilm")),
            Arrays.asList((String[]) rs.getArray("genres").getArray()),
            Arrays.asList((String[]) rs.getArray("actors").getArray()),
            rs.getInt("tahunrilis"),
            rs.getDouble("averagerating")
        );
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
            rs.getInt("idgenre"),
            rs.getString("nama")
        );
    }
}
