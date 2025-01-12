package com.example.demo.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.MethodBayarEnum;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcCartRepository implements CartRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addToCart(int idUser, int idFilm, int jumlahHari, int harga) {
        String sql = "INSERT INTO Cart (idUser, idFilm, jumlahHari, harga) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, idUser, idFilm, jumlahHari, harga);
    }

    @Override
    public List<Cart> findCartByUserId(int idUser) {
        String sql = """
        SELECT c.idCart, c.idUser, c.idFilm, c.jumlahHari, c.jumlah, c.harga, c.tanggalDitambahkan, f.judul AS judul, f.coverFilm AS cover
        FROM Cart c
        JOIN Film f ON c.idFilm = f.idFilm
        WHERE c.idUser = ?
        """;
        return jdbcTemplate.query(sql, this::mapRowToCartWithDetails, idUser);
    }

    @Override
    public void updateCart(int idUser, int idFilm, int jumlahHari) {
        String sql = "UPDATE Cart SET jumlahHari = ? WHERE idUser = ? AND idFilm = ?";
        jdbcTemplate.update(sql, jumlahHari, idUser, idFilm);
    }

    @Override
    public void removeFromCart(int idFilm) {
        String sql = "DELETE FROM Cart WHERE idFilm = ?";
        jdbcTemplate.update(sql, idFilm);
    }

    @Override
    public void clearCart(int idUser) {
        String sql = "DELETE FROM Cart WHERE idUser = ?";
        jdbcTemplate.update(sql, idUser);
    }

    private Cart mapRowToCartWithDetails(ResultSet resultSet, int rowNum) throws SQLException {
        Cart cart = new Cart(
            resultSet.getInt("idCart"),
            resultSet.getInt("idUser"),
            resultSet.getInt("idFilm"),
            resultSet.getInt("jumlahHari"),
            resultSet.getInt("jumlah"),
            resultSet.getInt("harga"),
            resultSet.getTimestamp("tanggalDitambahkan")
        );
        cart.setJudul(resultSet.getString("judul"));
        cart.setCover(resultSet.getBytes("cover"));
        cart.setNama("nama");
        return cart;
    }
}
