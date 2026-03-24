package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Student;
import br.com.fatecads.fatecads.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    // Dependency injection of the student's repository.
    @Autowired
    private StudentRepository studentRepository;

    public void save(Student student) {
        studentRepository.save(student);
    }
    // Method to listing all student's.
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
}
