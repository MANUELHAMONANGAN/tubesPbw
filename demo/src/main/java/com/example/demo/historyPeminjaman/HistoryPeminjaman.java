package com.example.demo.historyPeminjaman;

import java.util.Date;

import lombok.Data;

@Data
public class HistoryPeminjaman {
    private final Date tanggalPeminjaman;
    private final String judulFilm;
    private final int tahunRilis;
    private final int jumlahPinjam;
    private final int totalHari;
    private final int totalHarga;
}
