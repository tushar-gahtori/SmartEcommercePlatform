package com.example.SmartEcommercePlatform.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message="Name is required")
    private String name;

    @Email
    private String email;

    @Size(min=3, message="Password must be at least 4 characters")
    private String password;
}
