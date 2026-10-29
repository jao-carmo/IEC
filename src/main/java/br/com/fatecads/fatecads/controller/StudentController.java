package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Student;
import br.com.fatecads.fatecads.service.StudentService;
import br.com.fatecads.fatecads.service.CourseService;

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

    // Dependency injection of the course service.
    @Autowired
    private CourseService courseService;

    @Autowired
    private br.com.fatecads.fatecads.service.AcademicRegistrationService registration;

    @InitBinder("student")
    public void fields(org.springframework.web.bind.WebDataBinder binder) {
        binder.setAllowedFields("studentId", "fullName", "email", "phone", "address", "cpf", "registrationNumber", "course", "academicStatus");
    }

    // Method to save a student.
    @PostMapping("/save")
    public String save(@ModelAttribute Student student,
            @RequestParam(defaultValue = "false") boolean createAccount,
            @RequestParam(defaultValue = "") String accountLogin,
            @RequestParam(defaultValue = "") String accountPassword, Model model) {
        try {
            registration.saveStudent(student, createAccount, accountLogin, accountPassword);
            return "redirect:/student/list";
        } catch (IllegalArgumentException ex) {
            student.setAccount(student.getStudentId() == null ? null : java.util.Optional.ofNullable(studentService.findById(student.getStudentId())).map(Student::getAccount).orElse(null));
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("course", courseService.findAll());
            model.addAttribute("accountLogin", accountLogin);
            return "student/studentForm";
        }
    }

    // Method to list all student records.
    @GetMapping("/list")
    public String list(Model model) {
        var records = studentService.findAll();
        model.addAttribute("student", records);
        model.addAttribute("enrolledCount", records.stream().filter(s -> s.getAcademicStatus() != br.com.fatecads.fatecads.entity.AcademicStatus.COMPLETED).count());
        model.addAttribute("completedCount", records.stream().filter(s -> s.getAcademicStatus() == br.com.fatecads.fatecads.entity.AcademicStatus.COMPLETED).count());
        return "student/studentList";
    }

    // Method to show the form to create a new student.
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("course", courseService.findAll());
        return "student/studentForm";
    }

    // Method to delete a student record by ID.
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        studentService.deleteById(id);
        return "redirect:/student/list";
    }

    // Method to edit a student record by ID.
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Student student = studentService.findById(id);
        if (student == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND);
        model.addAttribute("student", student);
        model.addAttribute("course", courseService.findAll());
        return "student/studentForm";
    }
}
