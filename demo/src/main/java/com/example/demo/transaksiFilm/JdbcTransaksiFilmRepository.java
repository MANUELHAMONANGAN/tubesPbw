package com.example.demo.transaksiFilm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import com.example.demo.enums.StatusRent;


@Repository
public class JdbcTransaksiFilmRepository implements TransaksiFilmRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(TransaksiFilm transaksiFilm) {
        String sql = "INSERT INTO TransaksiFilm (idTransaksi, idFilm, totalHari, jumlah, totalHarga, batasPengembalian) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
                    
        jdbcTemplate.update(sql,
            transaksiFilm.getIdTransaksi(),
            transaksiFilm.getIdFilm(),
            transaksiFilm.getTotalHari(),
            transaksiFilm.getJumlah(),
            transaksiFilm.getTotalHarga(),
            transaksiFilm.getBatasPengembalian()
        );
    }

    @Override
    public List<TransaksiFilm> findTransaksiId(int idTransaksi) {
        String sql = "SELECT * FROM TransaksiFilm WHERE idTransaksi = ?";
        return jdbcTemplate.query(sql, this::mapRowToTransaksiFilm, idTransaksi);
    }

    @Override
    public void updateStatus(int idTransaksi, int idFilm, StatusRent status) {
        String sql = "UPDATE TransaksiFilm SET status = CAST(? AS status_rent) WHERE idTransaksi = ? AND idFilm = ?";
        jdbcTemplate.update(sql, status.getValue(), idTransaksi, idFilm);
    }

    @Override
    public void updateAllStatusByTransaksi(int idTransaksi, StatusRent status) {
        String sql = "UPDATE TransaksiFilm SET status = CAST(? AS status_rent) WHERE idTransaksi = ?";
        jdbcTemplate.update(sql, status.getValue(), idTransaksi);
    }

    @Override
    public boolean allFilmsDone(int idTransaksi) {
        String sql = "SELECT COUNT(*) FROM TransaksiFilm WHERE idTransaksi = ? AND status != 'done'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idTransaksi);
        return count == 0; // Jika semua film sudah done, return true
    }

    private TransaksiFilm mapRowToTransaksiFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new TransaksiFilm(
            resultSet.getInt("idTransaksi"),
            resultSet.getInt("idFilm"),
            resultSet.getInt("totalHari"),
            resultSet.getInt("jumlah"),
            resultSet.getInt("totalHarga"),
            StatusRent.fromString(resultSet.getString("status")),
            resultSet.getDate("batasPengembalian").toLocalDate()
        );
    }
    
}
