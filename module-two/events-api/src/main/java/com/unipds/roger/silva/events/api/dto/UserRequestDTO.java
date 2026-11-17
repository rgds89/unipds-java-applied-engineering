package com.unipds.roger.silva.events.api.dto;

import lombok.Builder;

@Builder
public record UserRequestDTO(String name, String email, String userPassword) {
}
