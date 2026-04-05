package com.mobilezbd.service;

import com.mobilezbd.dto.AuthRequest;
import com.mobilezbd.dto.AuthResponse;
import com.mobilezbd.dto.CustomerProfileDto;
import com.mobilezbd.dto.RegisterRequest;
import com.mobilezbd.dto.SellerProfileDto;
import com.mobilezbd.entity.CustomerAccount;
import com.mobilezbd.entity.User;
import com.mobilezbd.entity.UserRole;
import com.mobilezbd.exception.BusinessException;
import com.mobilezbd.exception.ResourceNotFoundException;
import com.mobilezbd.repository.CustomerAccountRepository;
import com.mobilezbd.repository.UserRepository;
import com.mobilezbd.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomerAccountRepository customerAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public UserService(UserRepository userRepository,
                       CustomerAccountRepository customerAccountRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.customerAccountRepository = customerAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
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

        customerAccountRepository.save(CustomerAccount.builder()
                .name(saved.getName())
                .email(saved.getEmail())
                .user(saved)
                .account("")
                .build());

        UserDetails principal = userDetailsService.loadUserByUsername(saved.getEmail());
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(principal))
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
        if (request.getEmail().equalsIgnoreCase(adminEmail) && request.getPassword().equals(adminPassword)) {
            User admin = userRepository.findByEmail(adminEmail)
                    .orElseGet(() -> userRepository.save(User.builder()
                            .name("Admin")
                            .email(adminEmail)
                            .password(passwordEncoder.encode(adminPassword))
                            .role(UserRole.ROLE_ADMIN)
                            .build()));
            UserDetails details = userDetailsService.loadUserByUsername(admin.getEmail());
            return AuthResponse.builder()
                    .token(jwtUtil.generateToken(details))
                    .name(admin.getName())
                    .email(admin.getEmail())
                    .role(admin.getRole().name())
                    .build();
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(details))
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public CustomerProfileDto getProfile(String email) {
        CustomerAccount account = customerAccountRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found"));

        return CustomerProfileDto.builder()
                .customerId(account.getCustomerId())
                .name(account.getName())
                .email(account.getEmail())
                .account(account.getAccount())
                .build();
    }

    public SellerProfileDto getSellerProfile(String email) {
        CustomerAccount account = customerAccountRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return SellerProfileDto.builder()
                .sellerId(account.getCustomerId())
                .name(account.getName())
                .email(account.getEmail())
                .account(account.getAccount())
                .role(user.getRole().name())
                .build();
    }
}
