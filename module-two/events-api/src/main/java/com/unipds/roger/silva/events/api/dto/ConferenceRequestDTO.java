package com.unipds.roger.silva.events.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ConferenceRequestDTO(
        @NotBlank(message = "Name cannot be blank")
        String name,
        String address) {
}
