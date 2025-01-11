package com.example.demo.laporan;

import java.util.List;

public interface TransaksiGraphRepository {
    public List<TransaksiGraph> getGraphDataThisMonth();
    public List<TransaksiGraph> getGraphDataFilterTanggal(String tanggalAwal, String tanggalAkhir);
}
