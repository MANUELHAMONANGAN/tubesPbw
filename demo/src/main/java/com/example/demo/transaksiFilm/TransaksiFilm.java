package com.example.demo.transaksiFilm;

import java.time.LocalDate;

import com.example.demo.enums.StatusRent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransaksiFilm {
    private int idTransaksi;
    private int idFilm;
    private int totalHari;
    private int jumlah;
    private int totalHarga;
    private StatusRent status;
    private LocalDate batasPengembalian;
}
