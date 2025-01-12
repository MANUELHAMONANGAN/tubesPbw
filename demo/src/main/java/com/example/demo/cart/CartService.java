package com.example.demo.cart;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.MethodBayarEnum;
import com.example.demo.enums.RentEnum;
import com.example.demo.enums.StatusRent;
import com.example.demo.transaksi.Transaksi;
import com.example.demo.transaksi.TransaksiRepository;
import com.example.demo.transaksiFilm.TransaksiFilm;
import com.example.demo.transaksiFilm.TransaksiFilmRepository;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private TransaksiRepository transaksiRepository;

    @Autowired
    private TransaksiFilmRepository transaksiFilmRepository;

    public List<Cart> getCartByUserId (int idUser) {
        return cartRepository.findCartByUserId(idUser);
    }

    public void clearCartByUserId (int idUser) {
        cartRepository.clearCart(idUser);
    }

    //bikin transaksi
    public Transaksi createTransaki (int idUser, List<Cart> cartItems, MethodBayarEnum metodePembayaran) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty. Cannot create a transaction");
        }

        int totalHarga = cartItems.stream().mapToInt(item -> item.getJumlahHari() * item.getHargaPerHari()).sum();

        Transaksi transaksi = new Transaksi();
        transaksi.setIdUser(idUser);
        transaksi.setTanggal(new Timestamp(System.currentTimeMillis()));
        transaksi.setTipeTransaksi(RentEnum.PINJAM);
        transaksi.setTotal(totalHarga);
        transaksi.setMetodePembayaran(metodePembayaran);

        int idTransaksi = transaksiRepository.save(transaksi);
        transaksi.setIdTransaksi(idTransaksi);

        for (Cart cartItem : cartItems) {
            TransaksiFilm transaksiFilm = new TransaksiFilm();
            transaksiFilm.setIdTransaksi(idTransaksi);
            transaksiFilm.setIdFilm(cartItem.getIdFilm());
            transaksiFilm.setTotalHari(cartItem.getJumlahHari());
            transaksiFilm.setJumlah(1);
            transaksiFilm.setTotalHarga(cartItem.getJumlahHari() * cartItem.getHargaPerHari());
            transaksiFilm.setStatus(StatusRent.DRAFT);
            LocalDate batasPengembalian = LocalDate.now().plusDays(cartItem.getJumlahHari());
            transaksiFilm.setBatasPengembalian(batasPengembalian);

            transaksiFilmRepository.save(transaksiFilm);
        }

        return transaksi;
    }
}
