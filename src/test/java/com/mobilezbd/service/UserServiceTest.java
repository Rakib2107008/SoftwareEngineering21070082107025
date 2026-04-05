package com.mobilezbd.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.mobilezbd.dto.AuthRequest;
import com.mobilezbd.dto.AuthResponse;
import com.mobilezbd.dto.RegisterRequest;
import com.mobilezbd.entity.CustomerAccount;
import com.mobilezbd.entity.User;
import com.mobilezbd.entity.UserRole;
import com.mobilezbd.exception.BusinessException;
import com.mobilezbd.repository.CustomerAccountRepository;
import com.mobilezbd.repository.UserRepository;
import com.mobilezbd.security.JwtUtil;
import java.lang.reflect.Field;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CustomerAccountRepository accountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtil jwtUtil;

    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        userService = new UserService(userRepository, accountRepository, passwordEncoder, authenticationManager, userDetailsService, jwtUtil);
        setField(userService, "adminEmail", "admin@mobilezbd.com");
        setField(userService, "adminPassword", "admin123");
    }

    @Test
    void testRegister_success() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Ruhan");
        req.setEmail("r@x.com");
        req.setPassword("123456");

        when(userRepository.existsByEmail("r@x.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(accountRepository.save(any(CustomerAccount.class))).thenAnswer(i -> i.getArgument(0));
        UserDetails details = org.springframework.security.core.userdetails.User.withUsername("r@x.com").password("x").authorities("ROLE_CUSTOMER").build();
        when(userDetailsService.loadUserByUsername("r@x.com")).thenReturn(details);
        when(jwtUtil.generateToken(details)).thenReturn("token");

        AuthResponse result = userService.register(req);
        assertEquals("token", result.getToken());
    }

    @Test
    void testRegister_sellerRole() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Seller One");
        req.setEmail("seller@x.com");
        req.setPassword("123456");
        req.setRole("SELLER");

        when(userRepository.existsByEmail("seller@x.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(11L);
            return u;
        });
        when(accountRepository.save(any(CustomerAccount.class))).thenAnswer(i -> i.getArgument(0));

        UserDetails details = org.springframework.security.core.userdetails.User
                .withUsername("seller@x.com")
                .password("x")
                .authorities("ROLE_SELLER")
                .build();
        when(userDetailsService.loadUserByUsername("seller@x.com")).thenReturn(details);
        when(jwtUtil.generateToken(details)).thenReturn("seller-token");

        AuthResponse result = userService.register(req);
        assertEquals("ROLE_SELLER", result.getRole());
    }

    @Test
    void testRegister_duplicateEmail() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("dup@x.com");
        when(userRepository.existsByEmail("dup@x.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> userService.register(req));
    }

    @Test
    void testLogin_validCredentials() {
        AuthRequest req = new AuthRequest();
        req.setEmail("u@x.com");
        req.setPassword("123456");

        UserDetails details = org.springframework.security.core.userdetails.User.withUsername("u@x.com").password("x").authorities("ROLE_CUSTOMER").build();
        when(userRepository.findByEmail("u@x.com")).thenReturn(Optional.of(User.builder().name("User").email("u@x.com").role(UserRole.ROLE_CUSTOMER).password("x").build()));
        when(userDetailsService.loadUserByUsername("u@x.com")).thenReturn(details);
        when(jwtUtil.generateToken(details)).thenReturn("ok");

        assertEquals("ok", userService.login(req).getToken());
    }

    @Test
    void testLogin_invalidPassword() {
        AuthRequest req = new AuthRequest();
        req.setEmail("u@x.com");
        req.setPassword("bad");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad"));
        assertThrows(BadCredentialsException.class, () -> userService.login(req));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
