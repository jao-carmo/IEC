package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.Course;
import br.com.fatecads.fatecads.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/save")
    public String save(@ModelAttribute Course course) {
        courseService.save(course);
        return "redirect:/course/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("course", courseService.findAll());
        return "course/courseList";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("course", new Course());
        return "course/courseForm";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        courseService.deleteById(id);
        return "redirect:/course/list";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("course", course);
        return "course/courseForm";
    }


}
