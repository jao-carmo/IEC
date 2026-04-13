package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Student;
import br.com.fatecads.fatecads.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

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

    // Method to delete a student record by ID.
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        studentService.deleteById(id);
        return "redirect:/student/list";
    }

    // Method to edit a student record by ID.
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        return "student/studentForm";
    }
}