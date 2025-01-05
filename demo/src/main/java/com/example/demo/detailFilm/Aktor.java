package com.example.demo.detailFilm;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Aktor {
    private final int id;
    private final String nama;
    private final String tanggallahir;
    private final String deskripsiDiri;
    private final String fotoProfil;
}
