package br.com.fatecads.fatecads.config;

import br.com.fatecads.fatecads.entity.User;
import br.com.fatecads.fatecads.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RootInitializer implements ApplicationRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String password;
    private final String email;

    public RootInitializer(UserRepository users, PasswordEncoder encoder,
            @Value("${app.root.password:}") String password,
            @Value("${app.root.email:root@fatecads.local}") String email) {
        this.users = users;
        this.encoder = encoder;
        this.password = password;
        this.email = email;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (users.findByLogin("root").isPresent() || password.isBlank()) return;
        if (password.length() < 12 || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
            throw new IllegalArgumentException("ROOT_PASSWORD must contain at least 12 characters and at most 72 UTF-8 bytes.");
        User root = new User();
        root.setFullName("System Administrator");
        root.setLogin("root");
        root.setEmail(email);
        root.setPassword(encoder.encode(password));
        root.setRole("ROLE_ADMIN");
        users.save(root);
    }
}
