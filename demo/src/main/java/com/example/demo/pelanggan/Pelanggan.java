package com.example.demo.pelanggan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pelanggan {
    private int idPelanggan;

    @NotBlank
    @Size(min = 4, max = 60)
    private String nama;
    @NotBlank
    @Size(max = 13)
    private String nomorTelepon;
    @NotBlank
    @Size(min = 4, max = 60)
    private String email;
    @NotBlank
    @Size(min = 4, max = 30)
    private String password;

}
