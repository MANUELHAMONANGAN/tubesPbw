package com.example.demo.transaksi;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.MethodBayarEnum;
import com.example.demo.enums.RentEnum;

// @Repository
// public class JdbcTransaksiRepository implements TransaksiRepository {
    
//     @Autowired
//     private final JdbcTemplate jdbcTemplate;
    
//     @Override
//     public int save(Transaksi transaksi) {
//         String sql = "INSERT INTO Transaksi (idUser, tanggal, tipeTransaksi, total, metodePembayaran) VALUES (?, ?, ?, ?, ?) RETURNING idTransaksi";
//         return jdbcTemplate.queryForObject(sql, Integer.class, transaksi.getIdUser(), transaksi.getTanggal(), transaksi.getTipeTransaksi().name(), transaksi.getTotal(), transaksi.getMetodePembayaran().name());
//     }

//     @Override
//     public Optional<Transaksi> findById(int idTransaksi) {
//         String sql = "SELECT * FROM Transaksi WHERE idTransaksi = ?";
//         return jdbcTemplate.query(sql, this::mapRowToTransaksi, idTransaksi).stream().findFirst();
//     }

//     @Override
//     public List<Transaksi> findAll() {
//         String sql = "SELECT * FROM Transaksi";
//         return jdbcTemplate.query(sql, this::mapRowToTransaksi);
//     }

//     private Transaksi mapRowToTransaksi(ResultSet resultSet, int rowNum) throws SQLException {
//         return new Transaksi(
//             resultSet.getInt("idTransaksi"),
//             resultSet.getInt("idUser"),
//             resultSet.getTimestamp("tanggal"),
//             RentEnum.valueOf(resultSet.getString("tipeTransaksi")),
//             resultSet.getInt("total"),
//             MethodBayarEnum.valueOf(resultSet.getString("metodePembayaran"))
//         );
//     }
// }
