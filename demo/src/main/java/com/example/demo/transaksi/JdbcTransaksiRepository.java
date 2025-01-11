package com.example.demo.transaksi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.MethodBayarEnum;
import com.example.demo.enums.RentEnum;

@Repository
public class JdbcTransaksiRepository implements TransaksiRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public int save(Transaksi transaksi) {
        String sql = "INSERT INTO Transaksi (idUser, tipeTransaksi, total, metodePembayaran) " +
                    "VALUES (?, ?::rent_enum, ?, ?::methodBayar_enum) RETURNING idTransaksi";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, transaksi.getIdUser());
            ps.setString(2, transaksi.getTipeTransaksi().toString());
            ps.setInt(3, transaksi.getTotal());
            ps.setString(4, transaksi.getMetodePembayaran().toString());
            return ps;
        }, keyHolder);
        
        return keyHolder.getKey().intValue();
    }

    @Override
    public Optional<Transaksi> findById(int idTransaksi) {
        String sql = "SELECT * FROM Transaksi WHERE idTransaksi = ?";
        return jdbcTemplate.query(sql, this::mapRowToTransaksi, idTransaksi).stream().findFirst();
    }

    @Override
    public List<Transaksi> findAll() {
        String sql = "SELECT * FROM Transaksi";
        return jdbcTemplate.query(sql, this::mapRowToTransaksi);
    }
    
    @Override
    public void updateStatus(int idTransaksi, RentEnum tipeTransaksi) {
        String sql = "UPDATE Transaksi SET tipeTransaksi = ? WHERE idTransaksi = ?";
        jdbcTemplate.update(sql, tipeTransaksi.name(), idTransaksi);
    }

    private Transaksi mapRowToTransaksi(ResultSet resultSet, int rowNum) throws SQLException {
        return new Transaksi(
            resultSet.getInt("idTransaksi"),
            resultSet.getInt("idUser"),
            resultSet.getTimestamp("tanggal"),
            RentEnum.fromString(resultSet.getString("tipeTransaksi")),
            resultSet.getInt("total"),
            MethodBayarEnum.fromString(resultSet.getString("metodePembayaran"))
        );
    }
}
