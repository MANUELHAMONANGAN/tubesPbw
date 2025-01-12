package com.example.demo.laporan;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTopFilmRepository implements TopFilmRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<TopFilm> getTop5BestFilmThisMonth(){
        String sql =
        """
        SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
        FROM
            (SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
            WHERE
                EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
                AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
            GROUP BY Film.judul
            ORDER BY JumlahPenyewaan DESC, Film.judul) as PenjualanFilm
        RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
        ORDER BY JumlahPenyewaan DESC, Film.judul
        LIMIT 5;
        """;

        List<TopFilm> list = jdbcTemplate.query(sql, this::mapRowToTopFilm);
        return list;
    }

    @Override
    public List<TopFilm> getTop5WorstFilmThisMonth(){
        String sql =
        """
        SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
        FROM
            (SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
            WHERE
                EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
                AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
            GROUP BY Film.judul
            ORDER BY JumlahPenyewaan ASC, Film.judul) as PenjualanFilm
        RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
        ORDER BY JumlahPenyewaan ASC, Film.judul
        LIMIT 5;
        """;

        List<TopFilm> list = jdbcTemplate.query(sql, this::mapRowToTopFilm);
        return list;
    }

    @Override
    public List<TopFilm> getListOutOfStockThisMonth(){
        String sql =
        """
        SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
        FROM
            (SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
            WHERE
                EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
                AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
            GROUP BY Film.judul
            ORDER BY JumlahPenyewaan DESC, Film.judul) as PenjualanFilm
        RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
        WHERE Film.stock = 0
        ORDER BY JumlahPenyewaan DESC, Film.judul;
        """;

        List<TopFilm> list = jdbcTemplate.query(sql, this::mapRowToTopFilm);
        return list;
    }

    @Override
    public List<TopFilm> getTop5BestFilmFilterTanggal(String tanggalAwal, String tanggalAkhir){
        String sql =
        """
        SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
        FROM
            (SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
            WHERE
                tanggal >= ?
                AND tanggal <= ?
            GROUP BY Film.judul
            ORDER BY JumlahPenyewaan DESC, Film.judul) as PenjualanFilm
        RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
        ORDER BY JumlahPenyewaan DESC, Film.judul
        LIMIT 5;
        """;

        Date tanggalAwalDate = Date.valueOf(tanggalAwal);
        Date tanggalAkhirDate = Date.valueOf(tanggalAkhir);

        List<TopFilm> list = jdbcTemplate.query(sql, this::mapRowToTopFilm, tanggalAwalDate, tanggalAkhirDate);
        return list;
    }

    @Override
    public List<TopFilm> getTop5WorstFilmFilterTanggal(String tanggalAwal, String tanggalAkhir){
        String sql =
        """
        SELECT Film.judul, COALESCE(JumlahPenyewaan,0) as JumlahPenyewaan , Film.stock, COALESCE(BanyakPendapatanPerFilm,0) as BanyakPendapatanPerFilm
        FROM
            (SELECT Film.judul, COALESCE(COUNT(TransaksiFilm.idFilm), 0) as JumlahPenyewaan, COALESCE(SUM(TransaksiFilm.totalHarga), 0) as BanyakPendapatanPerFilm
            FROM Transaksi
            INNER JOIN TransaksiFilm ON Transaksi.idTransaksi = TransaksiFilm.idTransaksi AND Transaksi.tipeTransaksi = 'Pinjam'
            INNER JOIN Film ON TransaksiFilm.idFilm = Film.idFilm
            WHERE
                tanggal >= ?
                AND tanggal <= ?
            GROUP BY Film.judul
            ORDER BY JumlahPenyewaan ASC, Film.judul) as PenjualanFilm
        RIGHT JOIN Film ON PenjualanFilm.judul = Film.judul
        ORDER BY JumlahPenyewaan ASC, Film.judul
        LIMIT 5;
        """;

        Date tanggalAwalDate = Date.valueOf(tanggalAwal);
        Date tanggalAkhirDate = Date.valueOf(tanggalAkhir);

        List<TopFilm> list = jdbcTemplate.query(sql, this::mapRowToTopFilm, tanggalAwalDate, tanggalAkhirDate);
        return list;
    }

    private TopFilm mapRowToTopFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new TopFilm(
            rowNum + 1,
            resultSet.getString("judul"),
            resultSet.getBigDecimal("JumlahPenyewaan"),
            resultSet.getInt("stock"),
            resultSet.getBigDecimal("BanyakPendapatanPerFilm")
        );
    }
}
