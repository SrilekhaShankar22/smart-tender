package com.smarttender.auth.service.impl;

import com.smarttender.auth.dto.request.*;
import com.smarttender.auth.dto.response.*;
import com.smarttender.auth.entity.*;
import com.smarttender.auth.repository.*;
import com.smarttender.auth.security.JwtTokenProvider;
import com.smarttender.auth.service.AuthService;
import com.smarttender.common.exception.ResourceNotFoundException;
import com.smarttender.common.exception.SmartTenderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-days:7}")
    private int refreshExpirationDays;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new SmartTenderException("EMAIL_EXISTS", "Email already registered: " + request.getEmail());
        }
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new SmartTenderException("ROLE_NOT_FOUND", "Default role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(Set.of(userRole))
                .build();
        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new SmartTenderException("INVALID_TOKEN", "Refresh token not found"));
        if (refreshToken.isRevoked()) throw new SmartTenderException("TOKEN_REVOKED", "Refresh token revoked");
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new SmartTenderException("TOKEN_EXPIRED", "Refresh token expired");
        }
        refreshTokenRepository.delete(refreshToken);
        return buildAuthResponse(refreshToken.getUser());
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        // TODO: Generate reset token, store in Redis, send email
        log.info("Password reset requested for: {}", request.getEmail());
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        // TODO: Validate token from Redis, update password
        log.info("Password reset for token: {}", request.getToken());
    }

    private AuthResponse buildAuthResponse(User user) {
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name()).collect(Collectors.toList());
        String accessToken = tokenProvider.generateToken(user.getEmail(), roles);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build();
        refreshTokenRepository.save(refreshToken);

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId()).email(user.getEmail())
                .firstName(user.getFirstName()).lastName(user.getLastName())
                .enabled(user.isEnabled()).emailVerified(user.isEmailVerified())
                .roles(roles.stream().collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken).refreshToken(refreshToken.getToken())
                .expiresIn(jwtExpirationMs / 1000).user(userResponse)
                .build();
    }
}
