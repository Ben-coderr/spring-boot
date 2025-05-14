package project.edusphere.assignment.repository;

import project.edusphere.assignment.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> { }
