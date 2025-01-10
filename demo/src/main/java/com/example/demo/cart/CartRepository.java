package com.example.demo.cart;

import java.util.List;

public interface CartRepository {
    void addToCart(int idUser, int idFilm, int jumlahHari);
    List<Cart> findCartByUserId(int idUser);
    void updateCart(int idCart, int jumlahHari);
    void removeFromCart(int idCart);
    void clearCart(int idUser);
}
