package com.unipds.roger.silva.events.api.service;

import com.unipds.roger.silva.events.api.dto.UserRequestDTO;
import com.unipds.roger.silva.events.api.exception.NotFoundException;
import com.unipds.roger.silva.events.api.model.User;
import com.unipds.roger.silva.events.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public User addUser(UserRequestDTO dto) {
        return userRepository.save(User.create(dto.name(), dto.email(), dto.userPassword()));
    }

    @Override
    public User updateUser(UserRequestDTO dto, Long id) {
        return userRepository.save(User.update(dto, getUserById(id)));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User {0} not found", id)));
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
}
