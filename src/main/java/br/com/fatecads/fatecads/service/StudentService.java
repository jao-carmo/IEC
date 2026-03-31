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
        normalizeTextFields(student);
        validateRequiredFields(student);

        // Normalize common masked inputs before persisting.
        student.setPhone(onlyDigits(student.getPhone()));
        student.setCpf(onlyDigits(student.getCpf()));

        if (student.getPhone().length() != 11) {
            throw new IllegalArgumentException("Telefone deve conter 11 digitos (com DDD).");
        }

        if (student.getCpf().length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 digitos.");
        }

        if (student.getFullName().length() > 40) {
            throw new IllegalArgumentException("Nome deve ter no maximo 40 caracteres.");
        }

        if (!isBlank(student.getEmail()) && student.getEmail().length() > 100) {
            throw new IllegalArgumentException("E-mail deve ter no maximo 100 caracteres.");
        }

        if (student.getAddress().length() > 100) {
            throw new IllegalArgumentException("Endereco deve ter no maximo 100 caracteres.");
        }

        studentRepository.save(student);
    }
    // Method to list all student records.
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    private void validateRequiredFields(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Dados do aluno nao informados.");
        }

        if (isBlank(student.getFullName())) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }

        if (isBlank(student.getAddress())) {
            throw new IllegalArgumentException("Endereco e obrigatorio.");
        }

        if (isBlank(student.getRegistrationNumber())) {
            throw new IllegalArgumentException("RA e obrigatorio.");
        }

        if (isBlank(student.getPhone())) {
            throw new IllegalArgumentException("Telefone e obrigatorio.");
        }

        if (isBlank(student.getCpf())) {
            throw new IllegalArgumentException("CPF e obrigatorio.");
        }
    }

    private String onlyDigits(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private void normalizeTextFields(Student student) {
        if (student == null) {
            return;
        }

        student.setFullName(trimToNull(student.getFullName()));
        student.setEmail(trimToNull(student.getEmail()));
        student.setAddress(trimToNull(student.getAddress()));
        student.setPhone(trimToNull(student.getPhone()));
        student.setCpf(trimToNull(student.getCpf()));
        student.setRegistrationNumber(trimToNull(student.getRegistrationNumber()));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
