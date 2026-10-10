package com.college.problem_report.controller;

import com.college.problem_report.model.Admin;
import com.college.problem_report.model.Problem;
import com.college.problem_report.repository.AdminRepository;
import com.college.problem_report.service.ProblemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProblemService service;

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "admin-register";
        }

        if (adminRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username already taken");
            return "admin-register";
        }

        Admin admin = new Admin();
        admin.setName(name);
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository.save(admin);

        model.addAttribute("success", "Registration successful! Please login.");
        return "admin-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        var optionalAdmin = adminRepository.findByUsernameAndPassword(username, password);

        if (optionalAdmin.isEmpty()) {
            model.addAttribute("error", "Invalid username or password");
            return "admin-login";
        }

        Admin admin = optionalAdmin.get();
        session.setAttribute("adminId", admin.getId());
        session.setAttribute("adminName", admin.getName());
        session.setAttribute("admin", true);

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String status,
                            HttpSession session, Model model) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin-login";
        }

        List<Problem> problems;
        if (status != null && !status.isEmpty()) {
            problems = service.getProblemsByStatus(status);
        } else {
            problems = service.getAllProblems();
        }

        List<Problem> allProblems = service.getAllProblems();

        long total = allProblems.size();
        long pending = service.getProblemsByStatus("PENDING").size();
        long inProgress = service.getProblemsByStatus("IN_PROGRESS").size();
        long resolved = service.getProblemsByStatus("RESOLVED").size();
        long rejected = service.getProblemsByStatus("REJECTED").size();

        java.util.Map<String, Long> categoryMap = new java.util.LinkedHashMap<>();
        categoryMap.put("WiFi / Internet", allProblems.stream().filter(p -> "WiFi / Internet".equals(p.getCategory())).count());
        categoryMap.put("Hostel", allProblems.stream().filter(p -> "Hostel".equals(p.getCategory())).count());
        categoryMap.put("Computer Lab", allProblems.stream().filter(p -> "Computer Lab".equals(p.getCategory())).count());
        categoryMap.put("Library", allProblems.stream().filter(p -> "Library".equals(p.getCategory())).count());
        categoryMap.put("Canteen", allProblems.stream().filter(p -> "Canteen".equals(p.getCategory())).count());
        categoryMap.put("Other", allProblems.stream().filter(p -> "Other".equals(p.getCategory())).count());

        model.addAttribute("problems", problems);
        model.addAttribute("filterStatus", status);
        model.addAttribute("total", total);
        model.addAttribute("pending", pending);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("resolved", resolved);
        model.addAttribute("rejected", rejected);
        model.addAttribute("categoryMap", categoryMap);
        model.addAttribute("adminName", session.getAttribute("adminName"));

        return "admin-dashboard";
    }

    @PostMapping("/update/{id}")
    public String updateProblem(@PathVariable Long id,
                                @RequestParam String status,
                                @RequestParam(required = false) String remark,
                                HttpSession session) {

        if (session.getAttribute("admin") == null) {
            return "redirect:/admin-login";
        }

        service.updateStatus(id, status, remark);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}