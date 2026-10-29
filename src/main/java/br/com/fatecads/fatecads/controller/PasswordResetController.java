package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @org.springframework.beans.factory.annotation.Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @PostMapping("/forgot-password")
    public String sendResetLink(@RequestParam("email") String email, Model model) {
        passwordResetService.requestReset(email, baseUrl);
        model.addAttribute("successMessage", "If the email exists, a reset link has been sent.");
        return "forgotPassword";
    }

    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "resetPassword";
    }

    @PostMapping("/reset-password")
    public String handleReset(@RequestParam("token") String token,
                              @RequestParam("password") String password,
                              Model model) {
        boolean reset = passwordResetService.resetPassword(token, password);
        if (reset) {
            return "redirect:/login?reset";
        }
        model.addAttribute("token", token);
        model.addAttribute("errorMessage", "The reset link is invalid or expired, or the password does not meet the 12-character minimum and 72-byte maximum.");
        return "resetPassword";
    }

}

