package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Integer> {
    java.util.Optional<Professor> findByAccountLogin(String login);
    java.util.Optional<Professor> findFirstByRM(String rm);

}
