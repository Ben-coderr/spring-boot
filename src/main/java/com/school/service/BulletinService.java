// src/main/java/com/school/service/BulletinService.java
package com.school.service;

import com.school.model.Student;
import com.school.repository.StudentRepository;
import com.school.repository.ResultRepository;
import com.school.repository.AttendanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class BulletinService {

    private final StudentRepository    studentRepo;
    private final ResultRepository     resultRepo;
    private final AttendanceRepository attendanceRepo;
    private final ResultService        resultService;   // <-- NEW

    public BulletinService(StudentRepository    studentRepo,
                           ResultRepository     resultRepo,
                           AttendanceRepository attendanceRepo,
                           ResultService        resultService   // <-- NEW
    ) {
        this.studentRepo    = studentRepo;
        this.resultRepo     = resultRepo;
        this.attendanceRepo = attendanceRepo;
        this.resultService  = resultService;          // <-- assign
    }

    /* ------------------------------------------------------------------ */

    public Map<String,Object> generate(Long studentId) {
        Student s = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));

        Map<String,Object> out = new LinkedHashMap<>();
        out.put("student", Map.of("id", s.getId(), "name", s.getFullName()));

        /* ---- 1. raw per-subject averages ----------------------------- */
        Map<Long,Double> raw = new HashMap<>();
        for (Object[] row : resultRepo.avgBySubject(studentId)) {
            raw.put((Long) row[0], (Double) row[1]);
        }
        out.put("subjects", raw);

        /* ---- 2. weighted finals (scheme-aware) ----------------------- */
        Map<Long,Double> finals = new HashMap<>();
        Long gradeId = s.getSchoolClass().getGrade().getId();

        for (Long subjectId : raw.keySet()) {
            double fin = resultService
                    .avgForStudentSubject(studentId, subjectId, gradeId);
            finals.put(subjectId, fin);
        }
        out.put("finals", finals);

        /* ---- 3. overall + attendance -------------------------------- */
        double overall = finals.values().stream()
                               .mapToDouble(Double::doubleValue)
                               .average().orElse(0d);
        out.put("overallAverage", overall);

        long total   = attendanceRepo.countByStudentId(studentId);
        long present = attendanceRepo.countByStudentIdAndStatus(studentId, "PRESENT");
        out.put("attendancePct", total == 0 ? 0d : present * 100.0 / total);

        return out;
    }
}
