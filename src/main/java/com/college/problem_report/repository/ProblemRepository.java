package com.college.problem_report.repository;

import com.college.problem_report.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByStatus(String status);
    List<Problem> findByStudentRoll(String studentRoll);
}
