package com.example.demo.transaksi;

import java.util.List;

import com.example.demo.enums.MethodBayarEnum;
import com.example.demo.enums.RentEnum;
import com.example.demo.transaksiFilm.TransaksiFilmRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//kelas DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransaksiRequest {
    private int idUser;
    private RentEnum tipeTransaksi;
    private int total;
    private MethodBayarEnum metodePembayaran;
    private List<TransaksiFilmRequest> transaksiFilm;
}
