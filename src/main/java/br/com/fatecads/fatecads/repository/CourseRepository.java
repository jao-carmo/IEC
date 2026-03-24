package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {

}