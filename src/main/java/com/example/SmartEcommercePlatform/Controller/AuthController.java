package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.AuthRequestDTO;
import com.example.SmartEcommercePlatform.Dto.AuthResponseDTO;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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

    @Operation(
            summary = "Authenticate user and generate token",
            description = "Validates user credentials and returns a Bearer JWT token valid for 1 hour."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody AuthRequestDTO request){
        AuthResponseDTO response=authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>("Login successful",response));
    }
}
