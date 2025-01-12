package com.example.demo.laporan;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcFilmDisewaRepository implements FilmDisewaRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<FilmDisewa> getFilmDisewaThisMonth(){
        String sql =
        """
        SELECT COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
        FROM Transaksi
        INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi
        WHERE Transaksi.tipeTransaksi = 'Done'
            AND EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
            AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
        """;

        List<FilmDisewa> list = jdbcTemplate.query(sql, this::mapRowToFilmDisewa);
        return list.size() == 0 ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<FilmDisewa> getFilmDisewaFilterTanggal(String tanggalAwal, String tanggalAkhir){
        String sql =
        """
        SELECT COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
        FROM Transaksi
        INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi
        WHERE Transaksi.tipeTransaksi = 'Done'
            AND tanggal >= ?
            AND tanggal <= ?
        """;

        Date tanggalAwalDate = Date.valueOf(tanggalAwal);
        Date tanggalAkhirDate = Date.valueOf(tanggalAkhir);

        List<FilmDisewa> list = jdbcTemplate.query(sql, this::mapRowToFilmDisewa, tanggalAwalDate, tanggalAkhirDate);
        return list.size() == 0 ? Optional.empty() : Optional.of(list.get(0));
    }

    private FilmDisewa mapRowToFilmDisewa(ResultSet resultSet, int rowNum) throws SQLException {
        return new FilmDisewa(
            resultSet.getBigDecimal("JumlahPenyewaan")
        );
    }
}
