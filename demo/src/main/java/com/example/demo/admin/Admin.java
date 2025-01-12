package com.example.demo.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Admin {
    private int idAdmin;

    @NotBlank
    @Size(min = 4, max = 60)
    private String nama;
    @NotBlank
    @Size(min = 13, max = 13)
    private String nomorTelepon;
    @NotBlank
    @Size(min = 4, max = 60)
    private String email;
    @NotBlank
    @Size(min = 4, max = 60)
    private String password;

}
