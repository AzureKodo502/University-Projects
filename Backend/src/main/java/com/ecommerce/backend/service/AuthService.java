package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.LoginRequest;
import com.ecommerce.backend.dto.RegisterRequest;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // REGISTRAZIONE
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email già in uso!");
        }

        User newUser = new User();
        newUser.setNome(request.getNome());
        newUser.setCognome(request.getCognome());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());

        return userRepository.save(newUser);
    }

    // LOGIN
    public User login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Utente non trovato");
        }

        User user = userOpt.get();
        // Controllo password
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Password errata");
        }

        return user;
    }
}
