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

    @Autowired
    private br.com.fatecads.fatecads.service.AcademicRegistrationService registration;

    @InitBinder("professor")
    public void fields(org.springframework.web.bind.WebDataBinder binder) {
        binder.setAllowedFields("professorId", "fullName", "phone", "graduation", "RM");
    }

    // Method to save a professor.
    @PostMapping("/save")
    public String save(@ModelAttribute Professor professor,
            @RequestParam(defaultValue = "false") boolean createAccount,
            @RequestParam(defaultValue = "") String accountEmail,
            @RequestParam(defaultValue = "") String accountLogin,
            @RequestParam(defaultValue = "") String accountPassword, Model model) {
        try {
            registration.saveProfessor(professor, createAccount, accountEmail, accountLogin, accountPassword);
            return "redirect:/professor/list";
        } catch (IllegalArgumentException ex) {
            professor.setAccount(professor.getProfessorId() == null ? null : java.util.Optional.ofNullable(professorService.findById(professor.getProfessorId())).map(Professor::getAccount).orElse(null));
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("accountEmail", accountEmail);
            model.addAttribute("accountLogin", accountLogin);
            return "professor/professorForm";
        }
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
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        professorService.deleteById(id);
        return "redirect:/professor/list";
    }

    // Method to edit a professor record by ID.
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Professor professor = professorService.findById(id);
        if (professor == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        model.addAttribute("professor", professor);
        return "professor/professorForm";
    }
}
