package com.example.demo.laporan;

import java.util.List;
import java.util.Optional;

public interface TopGenreRepository {
    public Optional<TopGenre> getTopGenreThisMonth();
    public List<TopGenre> getTop5GenreThisMonth();
    public Optional<TopGenre> getTopGenreFilterTanggal(String tanggalAwal, String tanggalAkhir);
    public List<TopGenre> getTop5GenreFilterTanggal(String tanggalAwal, String tanggalAkhir);
}
