package com.school.service;

import com.school.model.Attendance;
import com.school.repository.AttendanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceService {

    private final AttendanceRepository repo;

    public AttendanceService(AttendanceRepository repo) {
        this.repo = repo;
    }

    /** simple % present for one pupil */
    public Map<String,Object> percentage(Long studentId) {

        List<Attendance> rows = repo.findByStudentId(studentId);

        if(rows.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "no attendance data for student " + studentId);

        int total   = rows.size();
        int present = 0;
        for (Attendance a : rows)
            if ("PRESENT".equalsIgnoreCase(a.getStatus()))
                present++;

        double pct = (present * 100.0) / total;

        Map<String,Object> out = new HashMap<>();
        out.put("studentId", studentId);
        out.put("presentDays", present);
        out.put("totalDays",   total);
        out.put("percentage",  pct);
        return out;
    }
    public Map<String,Object> percentForStudent(Long id){
        return percentage(id);
    }
}
