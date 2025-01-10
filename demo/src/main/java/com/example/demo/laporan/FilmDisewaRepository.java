package com.example.demo.laporan;

import java.util.Optional;

public interface FilmDisewaRepository {
    public Optional<FilmDisewa> getFilmDisewaThisMonth();
}
