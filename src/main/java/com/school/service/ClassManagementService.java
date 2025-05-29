package com.school.service;

import com.school.model.SchoolClass;
import com.school.model.Student;
import com.school.model.Grade;
import com.school.repository.SchoolClassRepository;
import com.school.repository.StudentRepository;
import com.school.repository.GradeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;



@Service
public class ClassManagementService {

    private final SchoolClassRepository classRepo;
    private final StudentRepository    studentRepo;
    private final GradeRepository      gradeRepo;

    public ClassManagementService(SchoolClassRepository c,
                                  StudentRepository    s,
                                  GradeRepository      g) {
        this.classRepo   = c;
        this.studentRepo = s;
        this.gradeRepo   = g;
    }


    //elper to detect class size and if enough 
    public boolean hasSpace(Long classId) {
        SchoolClass c = classRepo.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "class "+classId+" ?"));

        long headcount = studentRepo.countBySchoolClass_Id(classId);
        Integer cap    = (c.getCapacity() == null) ? 30 : c.getCapacity();

        return headcount < cap;
    }

    //change student class
    @Transactional
    public void moveStudent(Long studentId, Long targetClassId) {

        Student kid = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student "+studentId+" ?"));

        SchoolClass target = classRepo.findById(targetClassId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "class "+targetClassId+" ?"));

        if (!hasSpace(targetClassId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "class full");

        kid.setSchoolClass(target);
        studentRepo.save(kid);
    }

    //method to promote class to next grade
    @Transactional
    public void promoteClass(Long classId, List<Long> repeaters) {

        SchoolClass c = classRepo.findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "class "+classId+" ?"));

        Grade next = gradeRepo.findByLevel(c.getGrade().getLevel() + 1)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "grade missing"));

        // create a new “shell” class in the next grade – same name for now
        SchoolClass newClass = new SchoolClass();
        newClass.setName(c.getName());
        newClass.setCapacity(c.getCapacity());
        newClass.setGrade(next);
        newClass.setSupervisor(c.getSupervisor());
        classRepo.save(newClass);

        // move everyone except repeaters
        List<Student> movers = new ArrayList<>();
        for (Student pupil : studentRepo.findBySchoolClass_Id(classId)) {
            if (repeaters == null || !repeaters.contains(pupil.getId())) {
                movers.add(pupil);
            }
        }

        for (Student student : movers) {
            student.setSchoolClass(newClass);
            studentRepo.save(student);
        }
    }

    //Method to get the average of a class 
    public List<StudentRank> rank(Long classId) {

        List<Student> pupils = studentRepo.findBySchoolClass_Id(classId);

        List<StudentRank> temp = new ArrayList<>();
        for (Student student : pupils) {
            Double avg = studentRepo.averageScore(student.getId());
            if (avg == null) avg = 0d;

            temp.add(new StudentRank(student, avg));
        }

        
        temp.sort((a, b) -> Double.compare(b.avg(), a.avg()));

        return temp;
    }


    //To be continued 
    public record StudentRank(Student pupil, double avg) { }
}
