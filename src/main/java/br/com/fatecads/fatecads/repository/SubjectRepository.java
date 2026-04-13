package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

}