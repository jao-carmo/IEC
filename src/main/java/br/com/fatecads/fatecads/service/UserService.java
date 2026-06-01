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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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