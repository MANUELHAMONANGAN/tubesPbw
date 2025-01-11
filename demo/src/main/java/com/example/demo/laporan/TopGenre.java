package com.example.demo.laporan;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopGenre {
    private int rowNumber;
    private String namaGenre;
    private BigDecimal jumlahPenyewaan;
}
