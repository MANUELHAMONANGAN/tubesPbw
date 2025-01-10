package com.example.demo.laporan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LaporanService {
    @Autowired
    private TransaksiGraphRepository transaksiGraphRepository;

    @Autowired
    private FilmDisewaRepository filmDisewaRepository;

    @Autowired
    private TopGenreRepository topGenreRepository;

    @Autowired
    private TopFilmRepository topFilmRepository;

    public List<List<Object>> getGraphDataThisMonth(){
        List<List<Object>> graphData = new ArrayList<>();
        this.transaksiGraphRepository.getGraphDataThisMonth().forEach(data -> {
            List<Object> row = new ArrayList<>();
            row.add(data.getTanggal().toString());
            row.add(data.getTotal().doubleValue());
            graphData.add(row);
        });

        return graphData;
    }

    public Optional<FilmDisewa> getFilmDisewaThisMonth(){
        return this.filmDisewaRepository.getFilmDisewaThisMonth();
    }

    public TopGenre getTopGenreThisMonth(){
        Optional<TopGenre> topGenre = this.topGenreRepository.getTopGenreThisMonth();
        if(topGenre.isPresent()){
            return topGenre.get();
        }else{
            return null;
        }
    }

    public List<TopFilm> getTop5BestFilmThisMonth(){
        return this.topFilmRepository.getTop5BestFilmThisMonth();
    }

    public List<TopFilm> getTop5WorstFilmThisMonth(){
        return this.topFilmRepository.getTop5WorstFilmThisMonth();
    }

    public List<TopFilm> getListOutOfStockThisMonth(){
        return this.topFilmRepository.getListOutOfStockThisMonth();
    }

    public List<TopGenre> getTop5GenreThisMonth(){
        return this.topGenreRepository.getTop5GenreThisMonth();
    }
}
