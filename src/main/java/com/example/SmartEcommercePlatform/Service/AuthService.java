package com.example.SmartEcommercePlatform.Service;

import com.example.SmartEcommercePlatform.Dto.AuthRequestDTO;
import com.example.SmartEcommercePlatform.Dto.AuthResponseDTO;
import com.example.SmartEcommercePlatform.Entity.User;
import com.example.SmartEcommercePlatform.Exception.BadRequestException;
import com.example.SmartEcommercePlatform.Exception.ResourceNotFoundException;
import com.example.SmartEcommercePlatform.Repository.UserRepository;
import com.example.SmartEcommercePlatform.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO login(AuthRequestDTO request) {

        //Find the user by email
        User user=userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        if(!passwordEncoder.matches(request.getPassword(),user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        String token= jwtUtil.generateToken(user.getEmail());

        return new AuthResponseDTO(token);
    }
}
