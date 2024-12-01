package com.messenger.backend.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthHandler {

    @Autowired
    private AuthService authService;

    @PostMapping
    public AuthResponse authenticate(@RequestBody AuthRequest request) {
        return authService.authenticate(request.username(), request.password());
    }
}
