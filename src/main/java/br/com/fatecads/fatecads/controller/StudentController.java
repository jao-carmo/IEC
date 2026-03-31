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
@RequestMapping("/student")
public class StudentController {

    // Dependency injection of the student service.
    @Autowired
    private StudentService studentService;

    // Method to save a student.
    @PostMapping("/save")
    public String save(@ModelAttribute Student student) {
        studentService.save(student);
        return "redirect:/student/list";
    }

    // Method to list all student records.
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("student", studentService.findAll());
        return "student/studentList";
    }

    // Method to show the form to create a new student.
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("student", new Student());
        return "student/studentForm";
    }
}