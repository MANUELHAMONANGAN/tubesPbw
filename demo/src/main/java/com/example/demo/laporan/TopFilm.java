package com.example.demo.laporan;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopFilm {
    private int rowNumber;
    private String judul;
    private BigDecimal jumlahPenyewaan;
    private int stock;
    private BigDecimal banyakPendapatan;
}
