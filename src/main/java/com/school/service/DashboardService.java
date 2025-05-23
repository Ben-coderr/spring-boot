package com.school.service;

import com.school.model.SchoolClass;
import com.school.repository.SchoolClassRepository;
import com.school.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Bare-bones snapshot of how full every class is right now.
 * No Stream API – just plain loops so it’s easier to read/step-debug.
 */
@Service
public class DashboardService {

    public record Occupancy(Long classId,
                            String name,
                            int capacity,
                            Long enrolled) { }

    private final SchoolClassRepository classRepo;
    private final StudentRepository     studentRepo;

    public DashboardService(SchoolClassRepository cRepo,
                            StudentRepository     sRepo) {

        this.classRepo   = cRepo;
        this.studentRepo = sRepo;
    }

    /**
     * Build the list one element at a time (no stream/collect).
     */
    public List<Occupancy> snapshot() {

        List<SchoolClass> all = classRepo.findAll();
        List<Occupancy>   out = new ArrayList<>();

        for (SchoolClass c : all) {
            int cap = (c.getCapacity() == null) ? 30 : c.getCapacity();
            Long enrolled = studentRepo.countBySchoolClass_Id(c.getId());
            out.add(new Occupancy(c.getId(), c.getName(), cap, enrolled));
        }

        return out;
    }
}
