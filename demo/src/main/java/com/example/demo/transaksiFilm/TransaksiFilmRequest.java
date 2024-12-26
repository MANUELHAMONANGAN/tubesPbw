package com.example.demo.transaksiFilm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//kelas DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransaksiFilmRequest {
    private int idFilm;
    private int totalHari;
    private int jumlah;
}
