package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.UserResponseDTO;
import com.example.SmartEcommercePlatform.Entity.User;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;  //Fake DB

    @Mock
    private ModelMapper modelMapper;    //Fake Mapper

    @InjectMocks
    private UserService userService;    //Real Service, injecting the fakes above

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserResponseDTO() {
        // 1. ARRANGE (Set up the fake data and rules)
        Long userId = 1L;
        User mockUser=new User();
        mockUser.setId(userId);
        mockUser.setName("Test User");

        UserResponseDTO mockResponseDTO=new UserResponseDTO();
        mockResponseDTO.setId(userId);
        mockResponseDTO.setName("Test User");

        // Telling the fake repository what to return when findById is called
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Telling the fake mapper what to return when map is called
        when(modelMapper.map(mockUser, UserResponseDTO.class)).thenReturn(mockResponseDTO);

        // 2. ACT (Call the actual method we are testing)
        UserResponseDTO result = userService.getUserById(userId);

        // 3. ASSERT (Verify the results)
        assertNotNull(result);
        assertEquals("Test User",result.getName());
        assertEquals(userId,result.getId());

        // Verifying the repository was actually called exactly once
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        // 1. ARRANGE
        Long userId = 99L;

        // Tell the fake repository to return empty (simulating user not found)
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // 2 & 3. ACT & ASSERT
        // Verify that calling the method throws your custom exception
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(userId)
        );

        assertEquals("User not found with id: 99", exception.getMessage());

        // Verify the mapper was NEVER called, because it should fail before reaching it
        verify(modelMapper, never()).map(any(), any());
    }
}
