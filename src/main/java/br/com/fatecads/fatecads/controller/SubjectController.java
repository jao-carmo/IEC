package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Subject;
import br.com.fatecads.fatecads.service.CourseService;
import br.com.fatecads.fatecads.service.ProfessorService;
import br.com.fatecads.fatecads.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private CourseService courseService;

    @PostMapping("/save")
    public String save(@ModelAttribute Subject subject) {
        subjectService.save(subject);
        return "redirect:/subject/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("subject", subjectService.findAll());
        return "subject/subjectList";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("subject", new Subject());
        model.addAttribute("professor", professorService.findAll());
        model.addAttribute("course", courseService.findAll());
        return "subject/subjectForm";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        subjectService.deleteById(id);
        return "redirect:/subject/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Subject subject = subjectService.findById(id);
        model.addAttribute("subject", subject);
        model.addAttribute("professor", professorService.findAll());
        model.addAttribute("course", courseService.findAll());
        return "subject/subjectForm";
    }


}
