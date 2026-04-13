package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.service.ProfessorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    // Dependency injection of the professor service.
    @Autowired
    private ProfessorService professorService;

    // Method to save a professor.
    @PostMapping("/save")
    public String save(@ModelAttribute Professor professor) {
        professorService.save(professor);
        return "redirect:/professor/list";
    }

    // Method to list all professor records.
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("professor", professorService.findAll());
        return "professor/professorList";
    }

    // Method to show the form to create a new professor.
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("professor", new Professor());
        return "professor/professorForm";
    }

    // Method to delete a professor record by ID.
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        professorService.deleteById(id);
        return "redirect:/professor/list";
    }

    // Method to edit a professor record by ID.
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Professor professor = professorService.findById(id);
        model.addAttribute("professor", professor);
        return "professor/professorForm";
    }
}