package com.example.demo.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean signUpCekEmail(User user){
        Optional<User> existingUserByEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            return false;
        }

        //user.setPassword(passwordEncoder.encode(user.getPassword())); //TODO: JANGAN LUPA DI ENCODE TERAKHIR

        try {
            userRepository.save(user);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public boolean signUpCekNoTelp(User user){
        Optional<User> existingUserByNoTelp = userRepository.findByNoTelp(user.getNomorTelepon());
        if(existingUserByNoTelp.isPresent()) {
            return false;
        }

        return true;
    }

    public User login(String email, String password){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // if (passwordEncoder.matches(password, user.getPassword())) { //TODO: JANGAN LUPA DI UNCOMMENT PAS UDH GA DI ENCODE PASSWORDNYA
            //     return user;
            // }

            if(password.equals(user.getPassword())){
                return user;
            }
        }
        return null;
    }
}
