package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.User;
import br.com.fatecads.fatecads.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    // Dependency injection of the user service
    @Autowired
    private UserService userService;

    // Method to save a user
    @PostMapping("/save")
    public String save(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/login?registered";
    }

    // Method to list all users
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("user", userService.findAll());
        return "user/userList";
    }

    // Method to create a new user and open a new form
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        return "user/userForm";
    }

    // Method to delete a user by ID
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        userService.deleteById(id);
        return "redirect:/users/list";
    }

    // Method to edit a user by ID
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user/userForm";
    }

}