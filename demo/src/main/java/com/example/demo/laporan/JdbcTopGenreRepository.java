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
public class JdbcTopGenreRepository implements TopGenreRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<TopGenre> getTopGenreThisMonth(){
        String sql =
        """
        SELECT Genre.nama, COALESCE(JumlahPenyewaan, 0) as JumlahPenyewaan
        FROM
            (SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
            INNER JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
            WHERE
                EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
                AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
            GROUP BY Genre.nama
            ORDER BY JumlahPenyewaan DESC, Genre.nama) as PenjualanGenre
        RIGHT JOIN Genre ON PenjualanGenre.nama = Genre.nama
        ORDER BY JumlahPenyewaan DESC, Genre.nama
        LIMIT 1;
        """;

        List<TopGenre> list = jdbcTemplate.query(sql, this::mapRowToTopGenre);
        return list.get(0).getJumlahPenyewaan().intValue() == 0 ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<TopGenre> getTop5GenreThisMonth(){
        String sql =
        """
        SELECT Genre.nama, COALESCE(JumlahPenyewaan, 0) as JumlahPenyewaan
        FROM
            (SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
            INNER JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
            WHERE
                EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
                AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
            GROUP BY Genre.nama
            ORDER BY JumlahPenyewaan DESC, Genre.nama) as PenjualanGenre
        RIGHT JOIN Genre ON PenjualanGenre.nama = Genre.nama
        ORDER BY JumlahPenyewaan DESC, Genre.nama
        LIMIT 5;
        """;

        List<TopGenre> list = jdbcTemplate.query(sql, this::mapRowToTopGenre);
        return list;
    }

    @Override
    public Optional<TopGenre> getTopGenreFilterTanggal(String tanggalAwal, String tanggalAkhir){
        String sql =
        """
        SELECT Genre.nama, COALESCE(JumlahPenyewaan, 0) as JumlahPenyewaan
        FROM
            (SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
            INNER JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
            WHERE
                tanggal >= ?
                AND tanggal <= ?
            GROUP BY Genre.nama
            ORDER BY JumlahPenyewaan DESC, Genre.nama) as PenjualanGenre
        RIGHT JOIN Genre ON PenjualanGenre.nama = Genre.nama
        ORDER BY JumlahPenyewaan DESC, Genre.nama
        LIMIT 1;
        """;

        Date tanggalAwalDate = Date.valueOf(tanggalAwal);
        Date tanggalAkhirDate = Date.valueOf(tanggalAkhir);

        List<TopGenre> list = jdbcTemplate.query(sql, this::mapRowToTopGenre, tanggalAwalDate, tanggalAkhirDate);
        return list.get(0).getJumlahPenyewaan().intValue() == 0 ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<TopGenre> getTop5GenreFilterTanggal(String tanggalAwal, String tanggalAkhir){
        String sql =
        """
        SELECT Genre.nama, COALESCE(JumlahPenyewaan, 0) as JumlahPenyewaan
        FROM
            (SELECT Genre.nama, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN FilmGenre ON TransaksiFilm.idFilm = FilmGenre.idFilm
            INNER JOIN Genre ON FilmGenre.idGenre = Genre.idGenre
            WHERE
                tanggal >= ?
                AND tanggal <= ?
            GROUP BY Genre.nama
            ORDER BY JumlahPenyewaan DESC, Genre.nama) as PenjualanGenre
        RIGHT JOIN Genre ON PenjualanGenre.nama = Genre.nama
        ORDER BY JumlahPenyewaan DESC, Genre.nama
        LIMIT 5;
        """;

        Date tanggalAwalDate = Date.valueOf(tanggalAwal);
        Date tanggalAkhirDate = Date.valueOf(tanggalAkhir);

        List<TopGenre> list = jdbcTemplate.query(sql, this::mapRowToTopGenre, tanggalAwalDate, tanggalAkhirDate);
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
