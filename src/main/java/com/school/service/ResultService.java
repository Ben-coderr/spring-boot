package com.school.service;

import com.school.model.Subject;
import com.school.repository.ResultRepository;
import com.school.repository.SubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResultService {

    private final ResultRepository    resultRepo;
    private final SubjectRepository   subjectRepo;

    public ResultService(ResultRepository resultRepo,
                         SubjectRepository subjectRepo) {
        this.resultRepo  = resultRepo;
        this.subjectRepo = subjectRepo;
    }

    //overall average for one student in one grade
    public double avgForStudent(Long studentId) {
        double coeffSum = 0, total = 0;

        for (Object[] row : resultRepo.avgBySubject(studentId)) {
            Long subjectId = (Long) row[0];
            double perSubject = avgForStudentSubject(studentId, subjectId);
            Subject subject = subjectRepo.findById(subjectId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "subject not found"));

            total    += perSubject * subject.getCoefficient();
            coeffSum += subject.getCoefficient();
        }
        return coeffSum == 0 ? 0 : total / coeffSum;
    }

    //average for one subject of one student in one grade
    public double avgForStudentSubject(Long studentId,
                                       Long subjectId) {

        Subject subject = subjectRepo.findById(subjectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "subject not found"));

        Double cc  = resultRepo.averageForStudentSubjectKind(studentId, subjectId, "CC");
        Double ex  = resultRepo.averageForStudentSubjectKind(studentId, subjectId, "EXAM");
        Double att = resultRepo.averageForStudentSubjectKind(studentId, subjectId, "ATTENDANCE");

        if (cc  == null) cc  = 0d;
        if (ex  == null) ex  = 0d;
        if (att == null) att = 0d;

        double sum = 0;
        // final average = Σ(mark * weight/100)
        sum += cc  * (subject.getCcWeight() == null ? 0 : subject.getCcWeight() / 100.0);
        sum += ex  * (subject.getExamWeight() == null ? 0 : subject.getExamWeight() / 100.0);
        sum += att * (subject.getAttendanceWeight() == null ? 0 : subject.getAttendanceWeight() / 100.0);

        return sum;
    }
}

