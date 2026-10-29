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
        if (student.getCpf() != null) student.setCpf(student.getCpf().replaceAll("\\D", ""));
        if (student.getCpf() == null || student.getCpf().length() != 11)
            throw new IllegalArgumentException("CPF must contain 11 digits.");
        if (student.getPhone() != null) student.setPhone(student.getPhone().replaceAll("\\D", ""));
        RecordValidation.text(student.getFullName(), 40, "Full name");
        RecordValidation.text(student.getAddress(), 100, "Address");
        RecordValidation.text(student.getRegistrationNumber(), 255, "Registration number");
        if (student.getPhone() == null || student.getPhone().length() < 10 || student.getPhone().length() > 11)
            throw new IllegalArgumentException("Phone must contain 10 or 11 digits.");
        if (student.getEmail() != null && !student.getEmail().isBlank()
                && (student.getEmail().length() > 100 || !student.getEmail().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")))
            throw new IllegalArgumentException("Enter a valid email.");
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
