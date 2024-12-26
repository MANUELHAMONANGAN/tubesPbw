package com.example.demo.transaksi;

import java.util.List;
import java.util.Optional;

public interface TransaksiRepository {
    int save (Transaksi transaksi);
    Optional<Transaksi> findById(int idTransaksi);
    List<Transaksi> findAll();
}
