
package com.example.loginapp.service;

import com.example.loginapp.dto.UserDTO;
import com.example.loginapp.entity.User;
import com.example.loginapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        User user = new User(userDTO.getName(), userDTO.getEmail(), userDTO.getPassword());
        return userRepository.save(user);
    }

    @Override
    public boolean login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.map(user -> user.getPassword().equals(password)).orElse(false);
    }
}
