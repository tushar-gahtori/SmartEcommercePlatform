package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.UserRequestDTO;
import com.example.SmartEcommercePlatform.Dto.UserResponseDTO;
import com.example.SmartEcommercePlatform.Dto.UserUpdateDTO;
import com.example.SmartEcommercePlatform.Entity.User;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository,ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserRequestDTO createUser(UserRequestDTO dto){
        User user = modelMapper.map(dto,User.class);
        User savedUser = userRepository.save(user);
        return  modelMapper.map(savedUser,UserRequestDTO.class);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO updateUser(Long id, @Valid UserUpdateDTO dto){
        User userFound=userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userFound.setName(dto.getName());
        userFound.setEmail(dto.getEmail());
        userRepository.save(userFound);
        return modelMapper.map(userFound,UserResponseDTO.class);
    }

}
