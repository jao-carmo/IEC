package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {

}