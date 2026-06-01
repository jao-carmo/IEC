package br.com.fatecads.fatecads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.fatecads.fatecads.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

}