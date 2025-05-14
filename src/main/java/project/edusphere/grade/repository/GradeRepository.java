package project.edusphere.grade.repository;

import project.edusphere.grade.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {   boolean existsByLevel(Integer level); }
