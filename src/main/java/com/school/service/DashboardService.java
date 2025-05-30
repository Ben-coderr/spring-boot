package com.school.service;

import com.school.model.SchoolClass;
import com.school.repository.SchoolClassRepository;
import com.school.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//simple snapshot of class occupancy using loops
@Service
public class DashboardService {

    public record Occupancy(Long classId,
                            String name,
                            int capacity,
                            Long enrolled) { } // simple data

    private final SchoolClassRepository classRepo; // access classes
    private final StudentRepository     studentRepo; // access students

    public DashboardService(SchoolClassRepository classRepo,
                            StudentRepository     studentRepo) {

        this.classRepo   = classRepo;
        this.studentRepo = studentRepo;
    }

    // build the list one element at a time
    public List<Occupancy> snapshot() {

        List<SchoolClass> all = classRepo.findAll();
        List<Occupancy>   out = new ArrayList<>();

        for (SchoolClass cls : all) {
            int cap = (cls.getCapacity() == null) ? 30 : cls.getCapacity(); // default 30
            Long enrolled = studentRepo.countBySchoolClass_Id(cls.getId());
            out.add(new Occupancy(cls.getId(), cls.getName(), cap, enrolled));
        }

        return out;
    }

    // single class occupancy
    public Occupancy one(Long classId) {
        SchoolClass cls = classRepo.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "class " + classId + " not found"));
        int cap = (cls.getCapacity() == null) ? 30 : cls.getCapacity();
        Long enrolled = studentRepo.countBySchoolClass_Id(classId);
        return new Occupancy(cls.getId(), cls.getName(), cap, enrolled);
    }
}
