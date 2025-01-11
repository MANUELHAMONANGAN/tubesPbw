package com.example.demo.laporan;

import java.util.Optional;

public interface FilmDisewaRepository {
    public Optional<FilmDisewa> getFilmDisewaThisMonth();
    public Optional<FilmDisewa> getFilmDisewaFilterTanggal(String tanggalAwal, String tanggalAkhir);
}
