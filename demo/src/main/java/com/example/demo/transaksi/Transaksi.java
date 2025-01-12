package com.example.demo.transaksi;

import java.sql.Timestamp;

import com.example.demo.enums.MethodBayarEnum;
import com.example.demo.enums.RentEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaksi {
    private int idTransaksi;
    private int idUser;
    private Timestamp tanggal;
    private RentEnum tipeTransaksi;
    private int total;
    private MethodBayarEnum metodePembayaran;

    private String nama;

    public Transaksi(int idTransaksi, int idUser, Timestamp tanggal, RentEnum tipeTransaksi, int total, MethodBayarEnum metodePembayaran) {
        this.idTransaksi = idTransaksi;
        this.idUser = idUser;
        this.tanggal = tanggal;
        this.tipeTransaksi = tipeTransaksi;
        this.total = total;
        this.metodePembayaran = metodePembayaran;
    }
}