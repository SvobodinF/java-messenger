package com.messenger.backend.auth;

import com.messenger.backend.secure.JwtService;
import com.messenger.backend.domain.User;
import com.messenger.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            System.out.println("Invalid username or password.");
            return null;
        }

        // Генерация токена
        String token = jwtService.generateToken(username, user.getId());

        // Возвращение AuthResponse с токеном и ID пользователя
        return new AuthResponse(user.getId(), token);
    }
}
