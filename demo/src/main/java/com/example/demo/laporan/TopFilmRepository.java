package com.example.demo.laporan;

import java.util.List;

public interface TopFilmRepository {
    public List<TopFilm> getTop5BestFilmThisMonth();
    public List<TopFilm> getTop5WorstFilmThisMonth();
    public List<TopFilm> getListOutOfStockThisMonth();
}
