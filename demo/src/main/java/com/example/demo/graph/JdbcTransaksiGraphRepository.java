package com.example.demo.graph;

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

    public List<TransaksiGraph> getAllGraphData(){
        String sql =
        """
        SELECT DATE(tanggal) AS tanggal, SUM(total) AS total
        FROM Transaksi
        WHERE tipeTransaksi = 'Pinjam'
        GROUP BY DATE(tanggal)
        ORDER BY tanggal
        """;

        List<TransaksiGraph> list = jdbcTemplate.query(sql, this::mapRowToTransaksiGraph);
        return list;
    }

    private TransaksiGraph mapRowToTransaksiGraph(ResultSet resultSet, int rowNum) throws SQLException {
        return new TransaksiGraph(
            resultSet.getObject("tanggal", LocalDate.class),
            resultSet.getBigDecimal("total")
        );
    }
}
