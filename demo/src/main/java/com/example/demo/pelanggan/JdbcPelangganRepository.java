package com.example.demo.pelanggan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcPelangganRepository implements PelangganRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void save(Pelanggan pelanggan) throws Exception {
        String sql = "INSERT INTO Pelanggan (nama, nomorTelepon, email, password) VALUES (?, ?, ?, ?)";
        
    }

    @Override
    public Optional<Pelanggan> findByEmail(String email) {
        String sql = "SELECT * FROM Pelanggan WHERE email = ?";
        List<Pelanggan> results = jdbcTemplate.query(sql, this::mapRowToPelanggan, email);
        return results.size() == 0 ? Optional.empty() : Optional.of(results.get(0));
    }

    private Pelanggan mapRowToPelanggan (ResultSet resultSet, int rowNum) throws SQLException {
        return new Pelanggan(
            resultSet.getInt("idPelanggan"),
            resultSet.getString("nama"),
            resultSet.getString("nomorTelepon"),
            resultSet.getString("email"),
            resultSet.getString("password")
        );
    }
}

    
