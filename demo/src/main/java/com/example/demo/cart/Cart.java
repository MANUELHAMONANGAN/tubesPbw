package com.example.demo.cart;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cart {
    private int idCart;
    private int idUser;
    private int idFilm;
    private int jumlahHari;
    private int jumlah;
    private Timestamp tanggalDitambahkan;
}
