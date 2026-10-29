package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    java.util.Optional<Student> findByAccountLogin(String login);
    java.util.Optional<Student> findFirstByRegistrationNumber(String registrationNumber);

}
