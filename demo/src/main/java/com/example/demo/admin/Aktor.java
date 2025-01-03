package com.example.demo.admin;

import java.sql.Date;

import lombok.Data;

@Data
public class Aktor {
    private final String idAktor;
    private final String foto;
    private final String nama;
    private final Date tanggal_lahir;
    private final String deskripsi_diri;
}
