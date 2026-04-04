package com.mobilezbd.controller;

import com.mobilezbd.dto.CustomerProfileDto;
import com.mobilezbd.service.UserService;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerAccountController {

    private final UserService userService;

    public CustomerAccountController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerProfileDto> profile(Principal principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getName()));
    }
}
