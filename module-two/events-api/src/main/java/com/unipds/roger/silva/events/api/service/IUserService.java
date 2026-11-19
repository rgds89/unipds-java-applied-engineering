package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.UserRequestDTO;
import com.unipds.roger.silva.events.api.model.User;

import java.util.List;

public interface IUserService {
    User addUser(UserRequestDTO dto);

    User updateUser(UserRequestDTO dto, Long id);

    void deleteUser(Long id);

    User getUserById(Long id);

    User getUserByName(String name);

    User getUserByEmail(String email);

    List<User> getAllUser();
}
