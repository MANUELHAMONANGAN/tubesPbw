package com.example.demo.pelanggan;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PelangganService {
    
    @Autowired
    private PelangganRepository pelangganRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean signUp(Pelanggan pelanggan){
        Optional<Pelanggan> existingPelanggan = pelangganRepository.findByEmail(pelanggan.getEmail());
        if (existingPelanggan.isPresent()) {
            return false;
        }

        pelanggan.setPassword(passwordEncoder.encode(pelanggan.getPassword()));

        try {
            pelangganRepository.save(pelanggan);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public Pelanggan login(String email, String password){
        Optional<Pelanggan> pelangganOptional = pelangganRepository.findByEmail(email);
        if (pelangganOptional.isPresent()) {
            Pelanggan pelanggan = pelangganOptional.get();

            if (passwordEncoder.matches(password, pelanggan.getPassword())) {
                return pelanggan;
            }
        }
        return null;
    }
}
