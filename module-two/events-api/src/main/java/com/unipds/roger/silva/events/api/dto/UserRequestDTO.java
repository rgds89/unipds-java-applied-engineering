package com.unipds.roger.silva.events.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserRequestDTO(
        @NotBlank(message = "Name cannot be blank or null")
        String name,

        @NotBlank(message = "Email cannot be blank or null")
        @Size(min = 20, max = 100, message = "The email must be between 20 and 100 characters long.")
        String email,

        @NotBlank(message = "Password cannot be blank or null")
        String userPassword) {
}
