package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.AuthDtos.AuthResponse;
import com.demoapi.apicomida.dtos.AuthDtos.LoginRequest;
import com.demoapi.apicomida.dtos.AuthDtos.RefreshTokenRequest;
import com.demoapi.apicomida.dtos.AuthDtos.RegisterRequest;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.RefreshToken;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.models.UserProfile;
import com.demoapi.apicomida.repositories.RefreshTokenRepository;
import com.demoapi.apicomida.repositories.UserAccountRepository;
import com.demoapi.apicomida.repositories.UserProfileRepository;
import com.demoapi.apicomida.security.JwtService;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;
    private final CurrentUserService currentUserService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            UserProfileRepository userProfileRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            UserService userService,
            CurrentUserService currentUserService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userAccountRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        }
        UserAccount user = new UserAccount();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setActive(true);
        user = userAccountRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        userProfileRepository.save(profile);
        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest request) {
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.refreshToken())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token not valid"));
        if (!"refresh".equals(jwtService.extractType(request.refreshToken()))
                || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return issueTokens(refreshToken.getUser());
    }

    @Transactional
    public void logout() {
        UserAccount user = currentUserService.requireCurrentUser();
        refreshTokenRepository.findByUserAndRevokedFalse(user).forEach(token -> token.setRevoked(true));
    }

    public com.demoapi.apicomida.dtos.UserDtos.UserResponse me() {
        return userService.toUserResponse(currentUserService.requireCurrentUser());
    }

    private AuthResponse issueTokens(UserAccount user) {
        refreshTokenRepository.findByUserAndRevokedFalse(user).forEach(token -> token.setRevoked(true));
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshTokenValue = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(604800));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(accessToken, refreshTokenValue, "Bearer", userService.toUserResponse(user));
    }
}
