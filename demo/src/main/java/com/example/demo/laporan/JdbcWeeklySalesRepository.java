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
public class JdbcWeeklySalesRepository implements WeeklySalesRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<WeeklySales> getWeeklySalesThisMonth(){
        String sql =
        """
        SELECT COALESCE(SUM(total) / (EXTRACT(WEEK FROM CURRENT_DATE) - EXTRACT(WEEK FROM DATE_TRUNC('month', CURRENT_DATE)) + 1), 0) as WeeklySales
        FROM Transaksi
        WHERE tipeTransaksi = 'Pinjam'
            AND EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
            AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
        """;

        List<WeeklySales> list = jdbcTemplate.query(sql, this::mapRowToWeeklySales);
        return list.get(0).getWeeklySales().intValue() == 0 ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public Optional<WeeklySales> getWeeklySalesFilterTanggal(String tanggalAwal, String tanggalAkhir){
        String sql =
        """
        SELECT COALESCE(SUM(total) / 4, 0) as WeeklySales
        FROM Transaksi
        WHERE tipeTransaksi = 'Pinjam'
            AND tanggal >= ?
            AND tanggal <= ?
        """;

        Date tanggalAwalDate = Date.valueOf(tanggalAwal);
        Date tanggalAkhirDate = Date.valueOf(tanggalAkhir);

        List<WeeklySales> list = jdbcTemplate.query(sql, this::mapRowToWeeklySales, tanggalAwalDate, tanggalAkhirDate);
        return list.get(0).getWeeklySales().intValue() == 0 ? Optional.empty() : Optional.of(list.get(0));
    }

    private WeeklySales mapRowToWeeklySales(ResultSet resultSet, int rowNum) throws SQLException {
        return new WeeklySales(
            resultSet.getBigDecimal("WeeklySales")
        );
    }
}
