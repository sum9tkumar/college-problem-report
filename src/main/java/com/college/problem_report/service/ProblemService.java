package com.college.problem_report.service;

import com.college.problem_report.model.Problem;
import com.college.problem_report.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository repository;

    public Problem saveProblem(Problem problem) {
        problem.setCreatedAt(LocalDateTime.now());
        problem.setUpdatedAt(LocalDateTime.now());
        problem.setStatus("PENDING");
        return repository.save(problem);
    }

    public List<Problem> getAllProblems() {
        return repository.findAll();
    }

    public Problem getProblemById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Problem> getProblemsByStudent(String roll) {
        return repository.findByStudentRoll(roll);
    }

    public List<Problem> getProblemsByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Problem updateStatus(Long id, String status, String remark) {
        Problem problem = repository.findById(id).orElse(null);
        if (problem != null) {
            problem.setStatus(status);
            problem.setAdminRemark(remark);
            problem.setUpdatedAt(LocalDateTime.now());
            return repository.save(problem);
        }
        return null;
    }

    public void deleteProblem(Long id) {
        repository.deleteById(id);
    }
}