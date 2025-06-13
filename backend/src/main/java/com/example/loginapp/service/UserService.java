package com.example.loginapp.service;

import com.example.loginapp.dto.UserDTO;
import com.example.loginapp.entity.User;
import java.util.Optional;

public interface UserService {
    User registerUser(UserDTO userDTO);
    Optional<User> login(String email, String password);
}