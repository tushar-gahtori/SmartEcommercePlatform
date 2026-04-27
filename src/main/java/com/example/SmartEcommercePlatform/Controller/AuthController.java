package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.AuthRequestDTO;
import com.example.SmartEcommercePlatform.Dto.AuthResponseDTO;
import com.example.SmartEcommercePlatform.Dto.UserRequestDTO;
import com.example.SmartEcommercePlatform.Dto.UserResponseDTO;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.AuthService;
import com.example.SmartEcommercePlatform.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;


    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> register(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("User registered successfully", user,
                201));
    }


    @Operation(
            summary = "Authenticate user and generate token",
            description = "Validates user credentials and returns a Bearer JWT token valid for 1 hour."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request){
        AuthResponseDTO response=authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>("Login successful",response));
    }
}
