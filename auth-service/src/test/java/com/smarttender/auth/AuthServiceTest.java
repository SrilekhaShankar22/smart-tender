package com.smarttender.auth;

import com.smarttender.auth.dto.request.LoginRequest;
import com.smarttender.auth.dto.request.RegisterRequest;
import com.smarttender.auth.dto.response.AuthResponse;
import com.smarttender.auth.entity.Role;
import com.smarttender.auth.entity.User;
import com.smarttender.auth.repository.RefreshTokenRepository;
import com.smarttender.auth.repository.RoleRepository;
import com.smarttender.auth.repository.UserRepository;
import com.smarttender.auth.security.JwtTokenProvider;
import com.smarttender.auth.service.impl.AuthServiceImpl;
import com.smarttender.common.exception.SmartTenderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider tokenProvider;
    @Mock AuthenticationManager authenticationManager;
    @InjectMocks AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 900000L);
        ReflectionTestUtils.setField(authService, "refreshExpirationDays", 7);
    }

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setPassword("password123");
        req.setFirstName("Test");
        req.setLastName("User");

        Role role = Role.builder().id(1L).name(Role.RoleName.ROLE_USER).build();
        User savedUser = User.builder().id(1L).email(req.getEmail())
                .firstName(req.getFirstName()).lastName(req.getLastName())
                .roles(Set.of(role)).build();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(any())).thenReturn("encodedPass");
        when(userRepository.save(any())).thenReturn(savedUser);
        when(tokenProvider.generateToken(any(), any())).thenReturn("jwt.token.here");
        when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthResponse response = authService.register(req);

        assertThat(response.getAccessToken()).isEqualTo("jwt.token.here");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any());
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("existing@example.com");
        req.setPassword("pass");
        req.setFirstName("A"); req.setLastName("B");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(SmartTenderException.class)
                .hasMessageContaining("already registered");
    }
}
