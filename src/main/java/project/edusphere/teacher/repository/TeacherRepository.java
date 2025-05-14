package project.edusphere.teacher.repository;

import project.edusphere.teacher.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // derived-query: Spring Data figures the JPQL out for you
    List<Teacher> findAllBySubjects_Id(Long subjectId);
    Optional<Teacher> findByEmail(String email);
}
