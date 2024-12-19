package com.example.demo.etalase;

import lombok.Data;

@Data
public class Film {
    private final int id;
    private final String judul;
    private final int stock;
    private final String cover;
    private final int hargaPerHari;
    private final String deskripsi;
    private final int durasi;
}
