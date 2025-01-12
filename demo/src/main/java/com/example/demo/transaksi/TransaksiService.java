package com.example.demo.transaksi;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.RentEnum;
import com.example.demo.enums.StatusRent;
import com.example.demo.transaksiFilm.TransaksiFilm;
import com.example.demo.transaksiFilm.TransaksiFilmRepository;

@Service
public class TransaksiService {

    @Autowired
    private TransaksiRepository transaksiRepository;

    @Autowired
    private TransaksiFilmRepository transaksiFilmRepository;

    public List<Transaksi> getAllTransaksi() {
        return transaksiRepository.findAll();
    }

    public List<TransaksiFilm> getTransaksiFilmsByTransaksiId(int idTransaksi) {
        return transaksiFilmRepository.findTransaksiId(idTransaksi);
    }

    public void approveTransaksi(int idTransaksi) {                     //approve dari admin
        transaksiFilmRepository.updateAllStatusByTransaksi(idTransaksi, StatusRent.ONGOING);
    }

    public void updateFilmStatus(int idTransaksi, int idFilm) {
        transaksiFilmRepository.updateStatus(idTransaksi, idFilm, StatusRent.DONE);
    }

    public void updateTypeIfAllDone(int idTransaksi) {
        if (transaksiFilmRepository.allFilmsDone(idTransaksi)) {
            transaksiRepository.updateStatus(idTransaksi, RentEnum.DONE);
        }
    }
}
