package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.AuthRequestDTO;
import com.example.SmartEcommercePlatform.Dto.AuthResponseDTO;
import com.example.SmartEcommercePlatform.Dto.UserRequestDTO;
import com.example.SmartEcommercePlatform.Dto.UserResponseDTO;
import com.example.SmartEcommercePlatform.Exception.BadRequestException;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.AuthService;
import com.example.SmartEcommercePlatform.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
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
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

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

    @Operation(summary = "Logout user", description = "Invalidates the current JWT token.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(jakarta.servlet.http.HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
            return ResponseEntity.ok(new ApiResponse<>("Successfully logged out", null, 200));
        }
        throw new com.example.SmartEcommercePlatform.Exception.BadRequestException("No valid token found to log out.");
    }
}
