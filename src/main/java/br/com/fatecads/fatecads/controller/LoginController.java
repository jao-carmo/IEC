package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @org.springframework.beans.factory.annotation.Autowired
    private br.com.fatecads.fatecads.repository.StudentRepository students;
    @org.springframework.beans.factory.annotation.Autowired
    private br.com.fatecads.fatecads.repository.ProfessorRepository professors;
    @GetMapping("/")
    public String index() { return "redirect:/fatecads"; }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(java.security.Principal principal, org.springframework.ui.Model model) {
        students.findByAccountLogin(principal.getName()).ifPresent(student -> model.addAttribute("academicStudent", student));
        professors.findByAccountLogin(principal.getName()).ifPresent(professor -> model.addAttribute("academicProfessor", professor));
        return "home";
    }
}
