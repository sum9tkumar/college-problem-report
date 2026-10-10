package com.college.problem_report.repository;

import com.college.problem_report.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollAndPassword(String roll, String password);
    Optional<Student> findByRoll(String roll);
}