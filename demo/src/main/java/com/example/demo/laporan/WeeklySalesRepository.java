package com.example.demo.laporan;

import java.util.Optional;

public interface WeeklySalesRepository {
    public Optional<WeeklySales> getWeeklySalesThisMonth();
    public Optional<WeeklySales> getWeeklySalesFilterTanggal(String tanggalAwal, String tanggalAkhir);
}
