package com.school.service;

import com.school.model.MarkComponent;
import com.school.model.SubjectGradeScheme;
import com.school.repository.ResultRepository;
import com.school.repository.SubjectGradeSchemeRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResultService {

    private final ResultRepository       resultRepo;
    private final SubjectGradeSchemeRepo schemeRepo;

    public ResultService(ResultRepository       resultRepo,
                         SubjectGradeSchemeRepo schemeRepo) {
        this.resultRepo = resultRepo;
        this.schemeRepo = schemeRepo;
    }

    //overall average for one student in one grade
    public double avgForStudent(Long studentId, Long gradeId) {

        var schemes = schemeRepo.findByGradeId(gradeId);
        if (schemes.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "no schemes for grade "+gradeId);

        double coeffSum = 0, total = 0;

        for (SubjectGradeScheme sch : schemes) {
            double perSubject = avgForStudentSubject(
                                    studentId,
                                    sch.getSubject().getId(),
                                    gradeId);           // delegate

            total     += perSubject * sch.getCoefficient();
            coeffSum  += sch.getCoefficient();
        }
        return (coeffSum == 0) ? 0 : total / coeffSum;
    }

    //average for one subject of one student in one grade
    public double avgForStudentSubject(Long studentId,
                                       Long subjectId,
                                       Long gradeId) {

        SubjectGradeScheme scheme = schemeRepo
                .findBySubjectIdAndGradeId(subjectId, gradeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "mark scheme not defined"));

        double sum = 0;
        for (MarkComponent component : scheme.getComponents()) {
            Double raw = resultRepo
                    .averageForStudentComponent(studentId, component.getId());

            if (raw == null) raw = 0d;
            sum += raw * component.getWeight() / 100.0;
        }
        return sum;
    }
}
