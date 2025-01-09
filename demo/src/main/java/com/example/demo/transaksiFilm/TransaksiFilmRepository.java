package com.example.demo.transaksiFilm;

import java.util.List;

public interface TransaksiFilmRepository {
    void save(TransaksiFilm transaksiFilm);
    List<TransaksiFilm> findTransaksiId(int idTransaksi);
}
