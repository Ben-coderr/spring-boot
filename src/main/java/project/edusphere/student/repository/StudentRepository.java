package project.edusphere.student.repository;

import project.edusphere.student.model.Student;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> { 

    Optional<Student> findByEmail(String email);
    long countBySchoolClass_Id(Long classId);

}

