package com.example.demo.admin;

import lombok.Data;

@Data
public class Film {
    private final String idFilm;
    private final String judul;
    private final String stok;
    private final String coverFilm;
    private final String harga;
    private final String deskripsi;
    private final String durasi;
    private final String tahun_rilis;
    private final String rating;

}
