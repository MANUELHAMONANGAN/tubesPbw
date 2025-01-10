// package com.example.demo.transaksi;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.example.demo.transaksiFilm.TransaksiFilm;
// import com.example.demo.transaksiFilm.TransaksiFilmRepository;
// import com.example.demo.transaksiFilm.TransaksiFilmRequest;

// @Service
// public class TransaksiService {

//     @Autowired
//     private TransaksiRepository transaksiRepository;

//     @Autowired
//     private TransaksiFilmRepository transaksiFilmRepository;

//     public void createTransaction(TransaksiRequest transaksiRequest) {
//         // Simpan transaksi ke tabel Transaksi
//         Transaksi transaksi = new Transaksi();
//         transaksi.setIdUser(transaksiRequest.getIdUser());
//         transaksi.setTipeTransaksi(transaksiRequest.getTipeTransaksi());
//         transaksi.setTotal(transaksiRequest.getTotal());
//         transaksi.setMetodePembayaran(transaksiRequest.getMetodePembayaran());

//         int idTransaksi = transaksiRepository.save(transaksi);

//         // Simpan detail film ke tabel TransaksiFilm
//         for (TransaksiFilmRequest filmRequest : transaksiRequest.getTransaksiFilm()) {
//             TransaksiFilm transaksiFilm = new TransaksiFilm();
//             transaksiFilm.setIdTransaksi(idTransaksi);
//             transaksiFilm.setIdFilm(filmRequest.getIdFilm());
//             transaksiFilm.setTotalHari(filmRequest.getTotalHari());
//             transaksiFilm.setJumlah(filmRequest.getJumlah());
//             transaksiFilm.setTotalHarga(filmRequest.getJumlah() * filmRequest.getTotalHari() * getFilmHarga(filmRequest.getIdFilm()));

//             transaksiFilmRepository.save(transaksiFilm);
//         }
//     }

//     private int getFilmHarga(int idFilm) {
//         // Panggil repository untuk mendapatkan harga film berdasarkan idFilm
//         return 10000; // Contoh nilai harga tetap
//     }
// }

