package com.example.loginapp.controller;

import com.example.loginapp.dto.UserDTO;
import com.example.loginapp.entity.User;
import com.example.loginapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8081")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserDTO userDTO) {
        User user = userService.registerUser(userDTO);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserDTO userDTO) {
        Optional<User> userOptional = userService.login(userDTO.getEmail(), userDTO.getPassword());

        Map<String, Object> responseBody = new HashMap<>();

        if (userOptional.isPresent()) {
            User loggedInUser = userOptional.get();
            responseBody.put("user_id", loggedInUser.getId());
            return ResponseEntity.ok(responseBody);
        } else {
            responseBody.put("error", "Credenciais inválidas.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }
    }
}