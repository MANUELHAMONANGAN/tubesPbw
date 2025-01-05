package com.example.demo.detailFilm;

import java.util.List;

import lombok.Data;

@Data
public class DetailFilm {
    private final int id;
    private final String judul;
    private final int stock;
    private final String cover;
    private final int hargaPerHari;
    private final String deskripsi;
    private final int durasi;
    private final List<String> genres;
    private final List<Integer> idAktor;
}
