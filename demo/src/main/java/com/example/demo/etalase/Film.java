package com.example.demo.etalase;

import java.util.List;

import lombok.Data;

@Data
public class Film {
    private final int id;
    private final String judul;
    private final String cover;
    private final List<String> genres;
    private final List<String> actors;
}
