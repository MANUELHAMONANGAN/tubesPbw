package com.example.demo.laporan;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransaksiGraph {
    private LocalDate tanggal;
    private BigDecimal total;
}
