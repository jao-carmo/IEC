package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    java.util.Optional<User> findByLogin(String login);
}
