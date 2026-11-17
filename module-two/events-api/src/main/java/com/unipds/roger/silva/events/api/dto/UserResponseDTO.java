package com.unipds.roger.silva.events.api.dto;

import lombok.Builder;

@Builder
public record UserResponseDTO(Long userId, String name, String email, String userPassword) {
}
