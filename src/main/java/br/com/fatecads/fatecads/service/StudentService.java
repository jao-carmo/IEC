package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Student;
import br.com.fatecads.fatecads.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    // Dependency injection of the student repository.
    @Autowired
    private StudentRepository studentRepository;

    public void save(Student student) {
        if (student == null) {
            return;
        }
        studentRepository.save(student);
    }

    // Method to list all student records.
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    // Method to delete a student record by ID.
    public void deleteById(Integer id) {
        studentRepository.deleteById(id);
    }

    // Method to find a student record by ID.
    public Student findById(Integer id) {
        return studentRepository.findById(id).orElse(null);
    }
}
