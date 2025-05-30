package com.school.service;

import com.school.repository.ResultRepository;
import com.school.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ClassRankingService { // rank students

    private final ResultRepository  results;
    private final StudentRepository students;

    public ClassRankingService(ResultRepository resultRepo,
                               StudentRepository studentRepo) {
        this.results  = resultRepo;
        this.students = studentRepo;
    }


    public List<Map<String,Object>> ranking(Long classId) {

        List<Object[]> rows = results.findClassRanking(classId);

        if (rows.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "no results for class " + classId);

        List<Map<String,Object>> out = new ArrayList<>();
        int rankNumber = 1;

        for (Object[] row : rows) {
            Long    studentId = (Long)   row[0];
            Double  average   = (Double) row[1];
            String  name      = students.findById(studentId)
                                  .map(student -> student.getFullName())
                                  .orElse("unknown");

            Map<String,Object> record = new HashMap<>();
            record.put("rank",       rankNumber++);
            record.put("studentId",  studentId);
            record.put("name",       name);
            record.put("average",    average);
            out.add(record);
        }
        return out;
    }
}
