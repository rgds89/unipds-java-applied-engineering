package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.UserRequestDTO;
import com.unipds.roger.silva.events.api.dto.UserResponseDTO;

public interface IUserService {
    UserResponseDTO addUser(UserRequestDTO dto);
    UserResponseDTO getUserById(Long id);
}
