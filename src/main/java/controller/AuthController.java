package controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.LoginDTO;
import entities.User;
import services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.registerUser(loginDTO.getUsername(), loginDTO.getPassword());
            return ResponseEntity.ok("Usuário registrado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Optional<User> user = userService.loginUser(loginDTO.getUsername(), loginDTO.getPassword());

        if (user.isPresent()) {
            return ResponseEntity.ok("Login bem-sucedido!");
        }
        return ResponseEntity.status(401).body("Credenciais inválidas");
    }

}
