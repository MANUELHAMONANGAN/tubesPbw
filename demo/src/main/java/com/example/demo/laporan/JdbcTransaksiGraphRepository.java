package com.example.demo.laporan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcTransaksiGraphRepository implements TransaksiGraphRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<TransaksiGraph> getGraphDataThisMonth(){
        String sql =
        """
        SELECT DATE(tanggal) AS tanggal, SUM(total) AS total
        FROM Transaksi
        WHERE tipeTransaksi = 'Pinjam'
            AND EXTRACT(MONTH FROM tanggal) = EXTRACT(MONTH FROM CURRENT_DATE)
            AND EXTRACT(YEAR FROM tanggal) = EXTRACT(YEAR FROM CURRENT_DATE)
        GROUP BY DATE(tanggal)
        ORDER BY tanggal;
        """;

        List<TransaksiGraph> list = jdbcTemplate.query(sql, this::mapRowToTransaksiGraph);
        return list;
    }

    @Override
    public List<TransaksiGraph> getGraphDataFilterTanggal(String tanggalAwal, String tanggalAkhir){
        String sql =
        """
        SELECT DATE(tanggal) AS tanggal, SUM(total) AS total
        FROM Transaksi
        WHERE tipeTransaksi = 'Pinjam'
            AND tanggal >= ?
            AND tanggal <= ?
        GROUP BY DATE(tanggal)
        ORDER BY tanggal;
        """;

        List<TransaksiGraph> list = jdbcTemplate.query(sql, this::mapRowToTransaksiGraph, tanggalAwal, tanggalAkhir);
        return list;
    }

    private TransaksiGraph mapRowToTransaksiGraph(ResultSet resultSet, int rowNum) throws SQLException {
        return new TransaksiGraph(
            resultSet.getObject("tanggal", LocalDate.class),
            resultSet.getBigDecimal("total")
        );
    }
}
