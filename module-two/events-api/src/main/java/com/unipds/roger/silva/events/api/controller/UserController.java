package com.unipds.roger.silva.events.api.controller;

import com.unipds.roger.silva.events.api.dto.UserRequestDTO;
import com.unipds.roger.silva.events.api.dto.UserResponseDTO;
import com.unipds.roger.silva.events.api.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/user")
    public ResponseEntity<UserResponseDTO> addUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseDTO.fromEntityToDto(userService.addUser(userRequestDTO)));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody @Valid UserRequestDTO userRequestDTO, Long id) {
        return ResponseEntity.ok(UserResponseDTO.fromEntityToDto(userService.updateUser(userRequestDTO, id)));
    }

    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponseDTO.fromEntityToDto(userService.getUserById(id)));
    }

    @GetMapping("/user")
    public ResponseEntity<List<UserResponseDTO>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser().stream().map(UserResponseDTO::fromEntityToDto).toList());
    }

    @GetMapping("/user/{name}")
    public ResponseEntity<UserResponseDTO> getUserByName(@PathVariable String name) {
        return ResponseEntity.ok(UserResponseDTO.fromEntityToDto(userService.getUserByName(name)));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserResponseDTO> getUserEmail(@PathVariable String email) {
        return ResponseEntity.ok(UserResponseDTO.fromEntityToDto(userService.getUserByEmail(email)));
    }
}
