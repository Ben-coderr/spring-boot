package project.edusphere.lesson.repository;

import project.edusphere.lesson.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllBySubject_Id(Long subjectId);
}
