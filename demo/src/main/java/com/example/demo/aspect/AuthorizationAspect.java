package com.example.demo.aspect;

import java.util.Arrays;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

@Aspect
@Component
public class AuthorizationAspect {
    @Autowired
    HttpSession session;
    
    @Before("@annotation(requiredRole)")
    public void checkAuthorization(RequiredRole requiredRole) throws Throwable{
         // 1. Memeriksa apakah session memiliki atribut "username"
        if (session.getAttribute("idUser") == null) { 
            // Pengguna belum login
            throw new SecurityException("User belum log in");
        }

        // 2. Memeriksa apakah ada role "*", jika ada, role apapun diperbolehkan
        String[] roles = requiredRole.value();
        if (Arrays.asList(roles).contains("*")) {
            return; // Role apapun diperbolehkan, lanjutkan eksekusi method
        }

        // 3. Memeriksa apakah role pengguna ada dalam daftar roles yang diizinkan
        String currUserRole = (String) session.getAttribute("role");
        if (!Arrays.asList(roles).contains(currUserRole)) {
            // Jika role pengguna tidak ada di dalam daftar yang diizinkan
            throw new SecurityException("User tidak diizinkan");
        }
    }
}
