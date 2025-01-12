package com.example.demo.transaksi;

import java.util.List;
import java.util.Optional;

import com.example.demo.enums.RentEnum;

public interface TransaksiRepository {
    int save (Transaksi transaksi);
    Optional<Transaksi> findById(int idTransaksi);
    void updateStatus(int idTransaksi, RentEnum tipeTransaksi);
    List<Transaksi> findAll();
}