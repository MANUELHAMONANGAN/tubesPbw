package com.example.demo.laporan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTopGenreRepository implements TopGenreRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<TopGenre> getTopGenreThisMonth(){
        String sql =
        """
        SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
        FROM Transaksi
        INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
        INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
        RIGHT JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
        WHERE
            EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
            AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
        GROUP BY Genre.nama
        ORDER BY JumlahPenyewaan DESC, Genre.nama
        LIMIT 1;
        """;

        List<TopGenre> list = jdbcTemplate.query(sql, this::mapRowToTopGenre);
        return list.size() == 0 ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<TopGenre> getTop5GenreThisMonth(){
        String sql =
        """
        SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
        FROM Transaksi
        INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
        INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
        RIGHT JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
        WHERE
            EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
            AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
        GROUP BY Genre.nama
        ORDER BY JumlahPenyewaan DESC, Genre.nama
        LIMIT 5;
        """;

        List<TopGenre> list = jdbcTemplate.query(sql, this::mapRowToTopGenre);
        return list;
    }

    private TopGenre mapRowToTopGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new TopGenre(
            rowNum + 1,
            resultSet.getString("nama"),
            resultSet.getBigDecimal("JumlahPenyewaan")
        );
    }
}
