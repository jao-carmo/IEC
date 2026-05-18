package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.User;
import br.com.fatecads.fatecads.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    // Dependency injection of the user service.
    @Autowired
    private UserService userService;

    // Method to save a user.
    @PostMapping("/save")
    public String save(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/login?registered";
    }

    // Method to show the form to create a new user.
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("user", new User());
        return "user/userForm";
    }
}