package com.example.demo.historyPeminjaman;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcHistoryPeminjamanRepository implements HistoryPeminjamanRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<HistoryPeminjaman> findAllTransaksiDone(int idUser) {
        String sql = """
                select tabelFinal.tanggal, tabelFinal.judul, tabelFinal.tahunrilis, tabelFinal.jumlah, tabelFinal.totalHari, tabelFinal.totalHarga
                from
                    ((select tanggal, idfilm, jumlah, totalhari, totalharga
                    from 
                        transaksi
                        inner join transaksifilm on transaksi.idtransaksi = transaksifilm.idtransaksi
                    where iduser = ? and status = 'done') as tabelTransaksi
                    inner join film on tabelTransaksi.idfilm = film.idfilm) as tabelFinal
                order by tabelFinal.tanggal desc
                """;
        return this.jdbcTemplate.query(sql, this::mapRowToHistoryPeminjaman, idUser);
    }

    @Override
    public List<HistoryPeminjaman> findFilteredTransaksi(int idUser, String tanggalAwal, String tanggalAkhir) {
        String sql = """
                select tabelFinal.tanggal, tabelFinal.judul, tabelFinal.tahunrilis, tabelFinal.jumlah, tabelFinal.totalHari, tabelFinal.totalHarga
                from
                    ((select tanggal, idfilm, jumlah, totalhari, totalharga
                    from 
                        transaksi
                        inner join transaksifilm on transaksi.idtransaksi = transaksifilm.idtransaksi
                    where iduser = ? and status = 'done') as tabelTransaksi
                    inner join film on tabelTransaksi.idfilm = film.idfilm) as tabelFinal
                where tabelFinal.tanggal >= CAST(? AS DATE) and tabelFinal.tanggal <= CAST(? AS DATE)
                order by tabelFinal.tanggal desc
                """;
        return this.jdbcTemplate.query(sql, this::mapRowToHistoryPeminjaman, idUser, tanggalAwal, tanggalAkhir);
    }

    private HistoryPeminjaman mapRowToHistoryPeminjaman(ResultSet rs, int rowNum) throws SQLException {
        return new HistoryPeminjaman(
            rs.getDate("tanggal"),
            rs.getString("judul"),
            rs.getInt("tahunrilis"),
            rs.getInt("jumlah"),
            rs.getInt("totalhari"),
            rs.getInt("totalharga")
        );
    }
}
