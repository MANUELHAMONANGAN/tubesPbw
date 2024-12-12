package com.example.demo.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private int idUser;

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
    private Role role; 
    @NotBlank
    @Size(min = 4, max = 30)
    private String password;

    public enum Role {
        PELANGGAN("Pelanggan"),
        ADMIN("Admin");

        private final String value;

        Role(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Role fromString(String role) {
            for (Role r : Role.values()) {
                if (r.value.equalsIgnoreCase(role)) {
                    return r;
                }
            }
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

}
