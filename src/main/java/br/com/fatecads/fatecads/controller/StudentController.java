package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Student;
import br.com.fatecads.fatecads.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/students")
public class StudentController {

    // Dependency injection of the student's service.
    @Autowired
    private StudentService studentService;

    // Method to save a student.
    @PostMapping("/save")
    public String save(@ModelAttribute Student student) {
        studentService.save(student);
        return "redirect:/students/list";
    }

    // Method to listing all student's.
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "student/listStudents";
    }
}