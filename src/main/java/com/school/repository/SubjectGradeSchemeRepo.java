package com.school.repository;

import com.school.model.SubjectGradeScheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectGradeSchemeRepo
        extends JpaRepository<SubjectGradeScheme, Long> {

    
    Optional<SubjectGradeScheme>
        findBySubjectIdAndGradeId(Long subjectId, Long gradeId);

    
    List<SubjectGradeScheme> findByGradeId(Long gradeId);
}
