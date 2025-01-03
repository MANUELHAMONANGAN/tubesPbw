package com.example.demo.admin;

import java.sql.Date;
import java.util.List;

public interface AdminRepository {
    List<Aktor> findAllAktor(int maxPage, int currentPage);
    List<Aktor> findAktorByName(String name, int maxPage, int currentPage);
    List<Aktor> findAktorById(int idAktor);
    int getCount();
    int getCountFilter(String name);

    void update(int idAktor, String nama, Date tanggallahir, String deskripsiDiri);
    void updateGambar(int idAktor, byte[] imagePath);
}
