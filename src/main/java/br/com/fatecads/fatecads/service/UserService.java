package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.User;
import br.com.fatecads.fatecads.repository.UserRepository;

@Service
public class UserService {

    // Dependency Injection of the user repository
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Method to save a user
    public User save(User user) {
        if (user.getUserId() != null) {
            User existingUser = userRepository.findById(user.getUserId()).orElse(null);
            if (existingUser != null && (user.getPassword() == null || user.getPassword().isBlank())) {
                user.setPassword(existingUser.getPassword());
            }
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required for a new user");
        }

        if (!user.getPassword().startsWith("$2a$")
                && !user.getPassword().startsWith("$2b$")
                && !user.getPassword().startsWith("$2y$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }

    // Method to list all users
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }
}
