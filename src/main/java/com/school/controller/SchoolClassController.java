package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.ClassRankingService;
import com.school.service.ClassManagementService;
import com.school.service.DashboardService;
import com.school.exception.ApiException;
import com.school.exception.ResourceNotFoundException;
import com.school.exception.BadRequestException;
import java.util.Map;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/classes")
public class SchoolClassController {

    private final SchoolClassRepository classRepo;
    private final ClassRankingService   rankingService;
    private final StudentRepository     studentRepo;
    private final LessonRepository      lessonRepo;
    private final AttendanceRepository  attendanceRepo;
    private final ResultRepository      resultRepo;
    private final ClassManagementService classManager;
    private final DashboardService      dashboardService;

    public SchoolClassController(SchoolClassRepository repo,
                                 ClassRankingService   rnk,
                                 StudentRepository     studentRepo,
                                 LessonRepository      lessonRepo,
                                 AttendanceRepository  attendanceRepo,
                                 ResultRepository      resultRepo,
                                 ClassManagementService classManager,
                                 DashboardService      dash) {
        this.classRepo = repo;
        this.rankingService = rnk;
        this.studentRepo = studentRepo;
        this.lessonRepo = lessonRepo;
        this.attendanceRepo = attendanceRepo;
        this.resultRepo = resultRepo;
        this.classManager = classManager;
        this.dashboardService = dash;
    }


    @GetMapping
    public List<SchoolClassDto> allClasses(){
        List<SchoolClass> all = classRepo.findAll();
        List<SchoolClassDto> out = new ArrayList<>();
        for (SchoolClass classEntity : all) {
            Long gradeId = (classEntity.getGrade() != null) ? classEntity.getGrade().getId() : null;
            out.add(new SchoolClassDto(classEntity.getId(), classEntity.getName(), gradeId));
        }
        return out;
    }

    @GetMapping("{id}")
    public SchoolClassDto findClass(@PathVariable Long id){
        try {
            SchoolClass classEntity = classRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("class " + id + " not found"));
            Long gradeId = (classEntity.getGrade()!=null) ? classEntity.getGrade().getId() : null;
            return new SchoolClassDto(classEntity.getId(), classEntity.getName(), gradeId);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @GetMapping("{id}/rank")
    public java.util.List<Map<String,Object>> rankingForClass(@PathVariable Long id) {
        findClass(id);
        return rankingService.ranking(id);
    }

    @GetMapping("{id}/students")
    public List<StudentDto> studentsInClass(@PathVariable Long id) {
        findClass(id);
        List<Student> all = studentRepo.findBySchoolClass_Id(id);
        List<StudentDto> out = new ArrayList<>();
        for (Student student : all) {
            out.add(StudentMapper.toDto(student));
        }
        return out;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SchoolClassDto createClass(@RequestBody SchoolClass body){
        try {
            if(body.getName()==null||body.getName().isBlank())
                throw new BadRequestException("name required");
            if(body.getGrade()==null)
                throw new BadRequestException("grade required");
            SchoolClass saved = classRepo.save(body);
            Long gradeId = (saved.getGrade()!=null) ? saved.getGrade().getId() : null;
            return new SchoolClassDto(saved.getId(), saved.getName(), gradeId);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @PutMapping("{id}")
    public SchoolClassDto updateClass(@PathVariable Long id,@RequestBody SchoolClass in){
        try {
            SchoolClass classEntity = classRepo.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("class " + id + " not found"));
            if(in.getName()!=null)     classEntity.setName(in.getName());
            if(in.getCapacity()!=null) classEntity.setCapacity(in.getCapacity());
            if(in.getGrade()!=null)    classEntity.setGrade(in.getGrade());
            if(in.getSupervisor()!=null)classEntity.setSupervisor(in.getSupervisor());
            SchoolClass saved = classRepo.save(classEntity);
            Long gradeId = (saved.getGrade()!=null) ? saved.getGrade().getId() : null;
            return new SchoolClassDto(saved.getId(), saved.getName(), gradeId);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @DeleteMapping("{id}")
    public void removeClass(@PathVariable Long id){ classRepo.deleteById(id); }

    // list lessons of a class
    @GetMapping("{id}/lessons")
    public List<LessonDto> lessons(@PathVariable Long id) {
        findClass(id); // ensure class exists
        List<Lesson> all = lessonRepo.findBySchoolClass_Id(id);
        List<LessonDto> out = new ArrayList<>();
        for (Lesson l : all) out.add(LessonDto.from(l));
        return out;
    }

    // aggregate attendance for class
    @GetMapping("{id}/attendance")
    public Map<String,Object> attendance(@PathVariable Long id,
                                         @RequestParam(required=false) String status,
                                         @RequestParam(required=false) java.time.LocalDate date) {
        findClass(id);
        List<Attendance> rows = attendanceRepo.findByStudent_SchoolClass_Id(id);
        if (date != null)
            rows = rows.stream().filter(a -> date.equals(a.getDate())).toList();
        if (status != null)
            rows = rows.stream().filter(a -> status.equalsIgnoreCase(a.getStatus())).toList();
        long present = rows.stream().filter(a -> "PRESENT".equalsIgnoreCase(a.getStatus())).count();
        Map<String,Object> out = new java.util.HashMap<>();
        out.put("classId", id);
        out.put("total", rows.size());
        out.put("present", present);
        return out;
    }

    // results for all students in class
    @GetMapping("{id}/results")
    public List<ResultDto> results(@PathVariable Long id) {
        findClass(id);
        List<Student> kids = studentRepo.findBySchoolClass_Id(id);
        List<ResultDto> out = new ArrayList<>();
        for (Student s : kids) {
            for (Result r : resultRepo.findByStudent_Id(s.getId())) {
                out.add(ResultDto.from(r));
            }
        }
        return out;
    }

    // promote class
    @RequestMapping(value="{id}/promote", method={RequestMethod.POST,RequestMethod.PUT})
    public void promote(@PathVariable Long id, @RequestBody(required=false) List<Long> repeaters) {
        classManager.promoteClass(id, repeaters);
    }

    // add a student to class
    @PutMapping("{cid}/students/{sid}")
    public void addStudent(@PathVariable("cid") Long classId,
                           @PathVariable("sid") Long studentId) {
        classManager.moveStudent(studentId, classId);
    }

    // remove student from class
    @DeleteMapping("{cid}/students/{sid}")
    public void removeStudent(@PathVariable("cid") Long classId,
                              @PathVariable("sid") Long studentId) {
        Student kid = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (kid.getSchoolClass() == null || !kid.getSchoolClass().getId().equals(classId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "student not in class");
        kid.setSchoolClass(null);
        studentRepo.save(kid);
    }

    // single class occupancy
    @GetMapping("{id}/occupancy")
    public DashboardService.Occupancy occupancy(@PathVariable Long id) {
        return dashboardService.one(id);
    }
}
