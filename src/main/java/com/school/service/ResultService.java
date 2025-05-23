package com.school.service;

import com.school.repository.ResultRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResultService {

    private final ResultRepository marks;

    public ResultService(ResultRepository repo) { this.marks = repo; }

    public double avgForStudent(Long sid) {

        Double val = marks.averageForStudent(sid);

        if (val == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no scores for student");

        return val;
    }

    public double avgForStudentSubject(Long sid, Long subId) {

        Double val = marks.averageForStudentAndSubject(sid, subId);

        if (val == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no scores for that subject");

        return val;
    }
}
