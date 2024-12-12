package com.example.demo.pelanggan;

import java.util.Optional;

public interface PelangganRepository {
    void save(Pelanggan pelanggan) throws Exception;
    Optional<Pelanggan> findByEmail (String email);
}
