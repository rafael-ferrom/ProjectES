
package com.example.loginapp.service;

import com.example.loginapp.dto.UserDTO;
import com.example.loginapp.entity.User;

public interface UserService {
    User registerUser(UserDTO userDTO);
    boolean login(String email, String password);
}
