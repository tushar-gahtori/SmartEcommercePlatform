package com.example.SmartEcommercePlatform.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotBlank(message="Name is required")
    private String name;

    @NotBlank(message="Email is required")
    @Email
    private String email;
}
