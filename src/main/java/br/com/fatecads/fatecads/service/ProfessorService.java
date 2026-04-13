package br.com.fatecads.fatecads.service;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {

    // Dependency injection of the professor repository.
    @Autowired
    private ProfessorRepository professorRepository;

    public void save(Professor professor) {
        if (professor == null) {
            return;
        }
        professorRepository.save(professor);
    }

    // Method to list all professor records.
    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    // Method to delete a professor record by ID.
    public void deleteById(Integer id) {
        professorRepository.deleteById(id);
    }

    // Method to find a professor record by ID.
    public Professor findById(Integer id) {
        return professorRepository.findById(id).orElse(null);
    }
}
