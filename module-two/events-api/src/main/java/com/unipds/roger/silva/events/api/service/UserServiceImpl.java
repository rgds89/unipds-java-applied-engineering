package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.UserRequestDTO;
import com.unipds.roger.silva.events.api.dto.UserResponseDTO;
import com.unipds.roger.silva.events.api.model.User;
import com.unipds.roger.silva.events.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserResponseDTO addUser(UserRequestDTO dto) {
        var user = userRepository.save(User.create(dto.name(), dto.email(), dto.userPassword()));
        return buildUserResponseDto(user);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return buildUserResponseDto(user);
    }

    private UserResponseDTO buildUserResponseDto(User user){
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .userPassword(user.getUserPassword())
                .build();
    }

}
