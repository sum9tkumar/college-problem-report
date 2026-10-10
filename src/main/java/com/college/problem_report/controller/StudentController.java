package com.college.problem_report.controller;

import com.college.problem_report.model.Problem;
import com.college.problem_report.model.Student;
import com.college.problem_report.repository.StudentRepository;
import com.college.problem_report.service.ProblemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private ProblemService service;

    @Autowired
    private StudentRepository studentRepository;

    // ===== REGISTER =====
    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String roll,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "student-register";
        }

        if (studentRepository.findByRoll(roll).isPresent()) {
            model.addAttribute("error", "Roll number already registered");
            return "student-register";
        }

        Student student = new Student();
        student.setName(name);
        student.setRoll(roll);
        student.setEmail(email);
        student.setPassword(password);
        studentRepository.save(student);

        model.addAttribute("success", "Registration successful! Please login.");
        return "student-login";
    }

    // ===== LOGIN =====
    @PostMapping("/login")
    public String login(@RequestParam String roll,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        var optionalStudent = studentRepository.findByRollAndPassword(roll, password);

        if (optionalStudent.isEmpty()) {
            model.addAttribute("error", "Invalid roll number or password");
            return "student-login";
        }

        Student student = optionalStudent.get();
        session.setAttribute("studentId", student.getId());
        session.setAttribute("studentName", student.getName());
        session.setAttribute("studentRoll", student.getRoll());

        return "redirect:/student/dashboard";
    }

    // ===== DASHBOARD =====
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("studentId") == null) {
            return "redirect:/student-login";
        }

        String roll = (String) session.getAttribute("studentRoll");
        model.addAttribute("studentName", session.getAttribute("studentName"));
        model.addAttribute("studentRoll", roll);
        model.addAttribute("problems", service.getProblemsByStudent(roll));
        return "student-dashboard";
    }

    // ===== SUBMIT PROBLEM =====
    @PostMapping("/submit")
    public String submitProblem(@RequestParam String category,
                                @RequestParam String title,
                                @RequestParam String description,
                                HttpSession session) {

        if (session.getAttribute("studentId") == null) {
            return "redirect:/student-login";
        }

        Problem problem = new Problem();
        problem.setStudentName((String) session.getAttribute("studentName"));
        problem.setStudentRoll((String) session.getAttribute("studentRoll"));
        problem.setCategory(category);
        problem.setTitle(title);
        problem.setDescription(description);

        service.saveProblem(problem);
        return "redirect:/student/dashboard";
    }

    // ===== LOGOUT =====
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}