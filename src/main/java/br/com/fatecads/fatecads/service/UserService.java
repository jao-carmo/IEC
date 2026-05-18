package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.User;
import br.com.fatecads.fatecads.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UserService {

    // Dependency injection of the user repository.
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Method to list all user records.
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Method to delete a user record by ID.
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    // Method to find a user record by ID.
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
}
