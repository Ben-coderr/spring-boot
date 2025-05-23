package com.school.service;

import com.school.repository.ResultRepository;
import com.school.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ClassRankingService {

    private final ResultRepository  results;
    private final StudentRepository students;

    public ClassRankingService(ResultRepository res,
                               StudentRepository stu) {
        this.results  = res;
        this.students = stu;
    }


    public List<Map<String,Object>> ranking(Long classId) {

        List<Object[]> rows = results.findClassRanking(classId);

        if (rows.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "no results for class " + classId);

        List<Map<String,Object>> out = new ArrayList<>();
        int rk = 1;

        for (Object[] r : rows) {
            Long    sid = (Long)   r[0];
            Double  avg = (Double) r[1];
            String  nm  = students.findById(sid)
                                  .map(s -> s.getFullName())
                                  .orElse("unknown");

            Map<String,Object> m = new HashMap<>();
            m.put("rank",       rk++);
            m.put("studentId",  sid);
            m.put("name",       nm);
            m.put("average",    avg);
            out.add(m);
        }
        return out;
    }
}
