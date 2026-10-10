package com.college.problem_report.controller;

import com.college.problem_report.model.Problem;
import com.college.problem_report.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    @Autowired
    private ProblemService problemService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/student-login")
    public String studentLogin() {
        return "student-login";
    }

    @GetMapping("/student-register")
    public String studentRegister() {
        return "student-register";
    }

    @GetMapping("/admin-login")
    public String adminLogin() {
        return "admin-login";
    }

    @GetMapping("/admin-register")
    public String adminRegister() {
        return "admin-register";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/track")
    public String trackPage() {
        return "track";
    }

    @PostMapping("/track")
    public String trackProblem(@RequestParam Long id, Model model) {
        Problem problem = problemService.getProblemById(id);
        if (problem == null) {
            model.addAttribute("error", "No problem found with ID: " + id);
        } else {
            model.addAttribute("problem", problem);
        }
        return "track";
    }
}