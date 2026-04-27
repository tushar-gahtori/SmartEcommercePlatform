package com.example.SmartEcommercePlatform.Controller;

import com.example.SmartEcommercePlatform.Dto.UserRequestDTO;
import com.example.SmartEcommercePlatform.Dto.UserResponseDTO;
import com.example.SmartEcommercePlatform.Dto.UserUpdateDTO;
import com.example.SmartEcommercePlatform.Entity.User;
import com.example.SmartEcommercePlatform.Response.ApiResponse;
import com.example.SmartEcommercePlatform.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "Get user details", description = "Requires a valid JWT token.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getUsers() {

        List<UserResponseDTO> users = userService.getAllUsers();

        return ResponseEntity.ok(
                new ApiResponse<>("Users fetched successfully", users, 200)
        );
    }


    @CacheEvict(value = "userDetails", key = "#dto.Email")
    @Operation(summary = "Update user details", description = "Requires a valid JWT token.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {

        UserResponseDTO updatedUser = userService.updateUser(id, dto);

        return ResponseEntity.ok(
                new ApiResponse<>("User updated successfully", updatedUser, 200)
        );
    }

    @Operation(summary = "Get user details by ID", description = "Requires a valid JWT token.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {

        UserResponseDTO user = userService.getUserById(id);

        return ResponseEntity.ok(
                new ApiResponse<>("User fetched successfully", user, 200)
        );
    }

    @Operation(summary = "Delete a user account", description = "Requires ADMIN role.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                new ApiResponse<>("User deleted successfully", null, 200)
        );
    }
}
