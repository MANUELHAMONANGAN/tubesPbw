package com.example.demo.transaksiFilm;

import java.util.List;

import com.example.demo.enums.StatusRent;

public interface TransaksiFilmRepository {
    void save(TransaksiFilm transaksiFilm);
    List<TransaksiFilm> findTransaksiId(int idTransaksi);
    void updateStatus(int idTransaksi, int idFilm, StatusRent status); // Update status untuk 1 film
    void updateAllStatusByTransaksi(int idTransaksi, StatusRent status); // Update semua status dalam transaksi
    boolean allFilmsDone(int idTransaksi);
}
