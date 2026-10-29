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
        User existing = user.getUserId() == null ? null : userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (user.getFullName() == null || user.getFullName().isBlank() || user.getFullName().length() > 40
                || user.getLogin() == null || !user.getLogin().matches("[A-Za-z0-9._-]{3,40}")
                || user.getEmail() == null || user.getEmail().length() > 40
                || !user.getEmail().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            throw new IllegalArgumentException("Enter a name, a valid email and a login of 3 to 40 letters, numbers, dots, underscores or hyphens.");
        userRepository.findByLogin(user.getLogin()).ifPresent(other -> {
            if (!other.getUserId().equals(user.getUserId())) throw new IllegalArgumentException("Login is already in use.");
        });
        userRepository.findByEmail(user.getEmail()).ifPresent(other -> {
            if (!other.getUserId().equals(user.getUserId())) throw new IllegalArgumentException("Email is already in use.");
        });
        if (!"ROLE_USER".equals(user.getRole()) && !"ROLE_ADMIN".equals(user.getRole()))
            throw new IllegalArgumentException("Choose a valid role.");
        if (existing != null && "root".equals(existing.getLogin())
                && (!"root".equals(user.getLogin()) || !"ROLE_ADMIN".equals(user.getRole())))
            throw new IllegalArgumentException("The root administrator cannot be renamed or demoted.");
        if (existing == null && "root".equalsIgnoreCase(user.getLogin()))
            throw new IllegalArgumentException("This login is reserved.");
        boolean keepPassword = existing != null && (user.getPassword() == null || user.getPassword().isBlank());
        if (!keepPassword && (user.getPassword() == null || user.getPassword().length() < 12
                || user.getPassword().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72))
            throw new IllegalArgumentException("Password must contain at least 12 characters and at most 72 UTF-8 bytes.");
        user.setPassword(keepPassword ? existing.getPassword() : passwordEncoder.encode(user.getPassword()));
        if (existing != null) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
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
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found."));
        if ("root".equals(user.getLogin())) throw new IllegalArgumentException("The root administrator cannot be deleted.");
        userRepository.deleteById(id);
    }
}
