package com.unipds.roger.silva.events.api.dto;

import com.unipds.roger.silva.events.api.model.User;
import lombok.Builder;

@Builder
public record UserResponseDTO(Long userId, String name, String email, String userPassword) {
    public static UserResponseDTO fromEntityToDto(User user){
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .userPassword(user.getUserPassword())
                .build();
    }
}
