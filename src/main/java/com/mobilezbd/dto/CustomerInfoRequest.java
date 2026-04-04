package com.mobilezbd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerInfoRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String mobileNumber;

    private String notes;
}
