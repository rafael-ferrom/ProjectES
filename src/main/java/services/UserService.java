package services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import entities.User;
import repositories.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Usuário já existe!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // Senha sem hash (não recomendado para produção)
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.filter(u -> u.getPassword().equals(password)); // Comparação direta (não seguro para produção)
    }
	
}
