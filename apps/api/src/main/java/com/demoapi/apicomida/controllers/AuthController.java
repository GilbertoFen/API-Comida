package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.AuthDtos.LoginRequest;
import com.demoapi.apicomida.dtos.AuthDtos.RefreshTokenRequest;
import com.demoapi.apicomida.dtos.AuthDtos.RegisterRequest;
import com.demoapi.apicomida.services.AuthService;
import com.demoapi.apicomida.util.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public Object register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/auth/login")
    public Object login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/auth/logout")
    public Object logout() {
        authService.logout();
        return ApiResponses.message("Logged out");
    }

    @PostMapping("/auth/refresh-token")
    public Object refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @GetMapping("/auth/me")
    public Object me() {
        return authService.me();
    }
}
