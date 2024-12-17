package com.example.demo.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.user.User.Role;

@Repository
public class JdbcUserRepository implements UserRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void save(User user) throws Exception {
        String sql = "INSERT INTO Users (nama, nomorTelepon, email, password) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getNama(), user.getNomorTelepon(), user.getEmail(), user.getPassword());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        List<User> results = jdbcTemplate.query(sql, this::mapRowToUser, email);
        return results.size() == 0 ? Optional.empty() : Optional.of(results.get(0));
    }

    private User mapRowToUser (ResultSet resultSet, int rowNum) throws SQLException {
        return new User(
            resultSet.getInt("idUser"),
            resultSet.getString("nama"),
            resultSet.getString("nomorTelepon"),
            resultSet.getString("email"),
            Role.fromString(resultSet.getString("role")),
            resultSet.getString("password")
        );
    }
}

    
