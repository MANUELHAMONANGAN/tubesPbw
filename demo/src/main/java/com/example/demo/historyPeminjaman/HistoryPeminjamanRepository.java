package com.example.demo.historyPeminjaman;

import java.util.List;

public interface HistoryPeminjamanRepository {
    List<HistoryPeminjaman> findAllTransaksiDone(int idUser);
    List<HistoryPeminjaman> findFilteredTransaksi(int idUser, String tanggalAwal, String tanggalAkhir);
}
