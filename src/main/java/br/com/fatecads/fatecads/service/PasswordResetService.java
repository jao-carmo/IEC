package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.User;
import br.com.fatecads.fatecads.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_TTL_MINUTES = 30;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectProvider<JavaMailSender> mailSenderProvider;

    public boolean requestReset(String email, String appUrl) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        String token = generateToken();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES));
        userRepository.save(user);

        sendResetEmail(user.getEmail(), appUrl, token);
        return true;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return true;
    }

    private void sendResetEmail(String email, String appUrl, String token) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            logger.warn("Mail sender not configured. Skipping password reset email.");
            return;
        }

        String resetLink = appUrl + "/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password reset request");
        message.setText("We received a password reset request.\n\n" +
                "Reset your password using the link below:\n" + resetLink + "\n\n" +
                "If you did not request this, please ignore this email.");

        try {
            mailSender.send(message);
        } catch (Exception ex) {
            logger.warn("Password reset email failed to send: {}", ex.getMessage());
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
