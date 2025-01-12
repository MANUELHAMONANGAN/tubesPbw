package com.example.demo.cart;

import java.sql.Timestamp;

import com.example.demo.enums.MethodBayarEnum;

import lombok.Data;

@Data
public class Cart {
    private int idCart;
    private int idUser;
    private int idFilm;
    private int jumlahHari;
    private int jumlah;
    private int hargaPerHari;
    private Timestamp tanggalDitambahkan;
    
    private String judul;
    private byte[] cover;
    private String coverBase64;
    private String nama;
    private MethodBayarEnum metodePembayaran;

    public Cart(int idCart, int idUser, int idFilm, int jumlahHari, int jumlah, int hargaPerHari, Timestamp tanggalDitambahkan) {
        this.idCart = idCart;
        this.idUser = idUser;
        this.idFilm = idFilm;
        this.jumlahHari = jumlahHari;
        this.jumlah = jumlah;
        this.hargaPerHari = hargaPerHari;
        this.tanggalDitambahkan = tanggalDitambahkan;
    }
}
