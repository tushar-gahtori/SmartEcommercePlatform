package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.*;
import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO dto);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
    UserResponseDTO updateUser(Long id, UserUpdateDTO dto);
    void deleteUser(Long id);
}