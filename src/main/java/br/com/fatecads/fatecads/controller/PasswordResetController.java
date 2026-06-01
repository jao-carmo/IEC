package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @PostMapping("/forgot-password")
    public String sendResetLink(@RequestParam("email") String email, HttpServletRequest request, Model model) {
        String appUrl = getAppUrl(request);
        boolean requested = passwordResetService.requestReset(email, appUrl);
        if (requested) {
            model.addAttribute("successMessage", "If the email exists, a reset link has been sent.");
        } else {
            model.addAttribute("errorMessage", "Email not found. Please check and try again.");
        }
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
        model.addAttribute("errorMessage", "Reset token is invalid or expired.");
        return "resetPassword";
    }

    private String getAppUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        return scheme + "://" + serverName + ":" + serverPort + contextPath;
    }
}

