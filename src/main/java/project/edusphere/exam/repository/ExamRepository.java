package project.edusphere.exam.repository;

import project.edusphere.exam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamRepository extends JpaRepository<Exam, Long> { }
