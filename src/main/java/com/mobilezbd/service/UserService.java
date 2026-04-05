package com.mobilezbd.service;

import com.mobilezbd.dto.AuthRequest;
import com.mobilezbd.dto.AuthResponse;
import com.mobilezbd.dto.RegisterRequest;
import com.mobilezbd.entity.User;
import com.mobilezbd.entity.UserRole;
import com.mobilezbd.exception.BusinessException;
import com.mobilezbd.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        UserRole registrationRole = parseRegistrationRole(request.getRole());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(registrationRole)
                .build();

        User saved = userRepository.save(user);
        return AuthResponse.builder()
                .token("registered")
                .name(saved.getName())
                .email(saved.getEmail())
                .role(saved.getRole().name())
                .build();
    }

    private UserRole parseRegistrationRole(String roleText) {
        if (roleText == null || roleText.isBlank()) {
            return UserRole.ROLE_CUSTOMER;
        }

        String normalized = roleText.trim().toUpperCase(Locale.ROOT);
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }

        UserRole parsed;
        try {
            parsed = UserRole.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid role. Allowed roles: CUSTOMER, SELLER");
        }

        if (parsed == UserRole.ROLE_ADMIN) {
            throw new BusinessException("Admin registration is not allowed");
        }
        return parsed;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return AuthResponse.builder()
                .token("authenticated")
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
