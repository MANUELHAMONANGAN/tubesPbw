package com.example.demo.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcCartRepository implements CartRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addToCart(int idUser, int idFilm, int jumlahHari) {
        String sql = "INSERT INTO Cart (idUser, idFilm, jumlahHari) VALUES (?, ?, ?) " +
                     "ON CONFLICT (idUser, idFilm) DO UPDATE SET jumlahHari = ?";           //kalau ada yang sama (brarti udh ada di cart) bakal update jumlah hari
        jdbcTemplate.update(sql, idUser, idFilm, jumlahHari, jumlahHari);
    }

    @Override
    public List<Cart> findCartByUserId(int idUser) {
        String sql = "SELECT * FROM Cart WHERE idUser = ?";
        return jdbcTemplate.query(sql, this::mapRowToCart, idUser);
    }

    @Override
    public void updateCart(int idCart, int jumlahHari) {
        String sql = "UPDATE Cart SET jumlahHari = ? WHERE idCart = ?";
        jdbcTemplate.update(sql, jumlahHari, idCart);
    }

    @Override
    public void removeFromCart(int idCart) {
        String sql = "DELETE FROM Cart WHERE idCart = ?";
        jdbcTemplate.update(sql, idCart);
    }

    @Override
    public void clearCart(int idUser) {
        String sql = "DELETE FROM Cart WHERE idUser = ?";
        jdbcTemplate.update(sql, idUser);
    }

    private Cart mapRowToCart(ResultSet resultSet, int rowNum) throws SQLException {
        return new Cart(
            resultSet.getInt("idCart"),
            resultSet.getInt("idUser"),
            resultSet.getInt("idFilm"),
            resultSet.getInt("jumlahHari"),
            resultSet.getInt("jumlah"), 
            resultSet.getTimestamp("tanggalDitambahkan")
        );
    }
}
