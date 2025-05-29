package com.school.controller;

import com.school.dto.*;
import com.school.model.Role;
import com.school.model.Student;
import com.school.model.User;
import com.school.model.Result;
import com.school.model.Attendance;
import com.school.repository.*;
import com.school.service.AttendanceService;
import com.school.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.model.Parent;
import com.school.model.SchoolClass;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentRepository     studentRepo;
    private final SchoolClassRepository classRepo;
    private final StudentService        studentService;
    private final AttendanceService     attendanceService;
    private final PasswordEncoder       passwordEncoder;
    private final UserRepository        userRepo;
    private final ParentRepository      parentRepo;
    private final ResultRepository      resultRepo;
    private final AttendanceRepository  attendanceRepo;
    private final LessonRepository      lessonRepo;

    public StudentController(
        StudentRepository     studentRepo,
        SchoolClassRepository classRepo,
        StudentService        service,
        AttendanceService     attendance,
        PasswordEncoder       passwordEncoder,
        UserRepository        userRepo,
        ParentRepository      parentRepo,
        ResultRepository      resultRepo,
        AttendanceRepository  attendanceRepo,
        LessonRepository      lessonRepo
    ) {
        this.studentRepo        = studentRepo;
        this.classRepo          = classRepo;
        this.studentService     = service;
        this.attendanceService  = attendance;
        this.passwordEncoder    = passwordEncoder;
        this.userRepo           = userRepo;
        this.parentRepo         = parentRepo;
        this.resultRepo         = resultRepo;
        this.attendanceRepo     = attendanceRepo;
        this.lessonRepo         = lessonRepo;
    }

    

    @GetMapping
    public List<StudentDto> allStudents() {
        List<Student> all = studentRepo.findAll();
        List<StudentDto> out = new ArrayList<>();
        for (Student student : all) {
            out.add(StudentMapper.toDto(student));
        }
        return out;
    }

    @GetMapping("{id}")
    public StudentDto findStudent(@PathVariable Long id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));
        return StudentMapper.toDto(student);
    }

    @GetMapping("{id}/attendance/percentage")
    public Map<String, Object> attendancePercentage(@PathVariable Long id) {
        findStudent(id);                                // ensure student exists
        return attendanceService.percentForStudent(id);
    }

    //create student

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentDto registerStudent(@RequestBody StudentReq body) {

        if (body.password() == null || body.password().isBlank())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "password required");

        User user = new User();
        String uname = (body.email() != null && !body.email().isBlank())
                     ? body.email()
                     : body.phone();

        if (userRepo.findByUsername(uname).isPresent())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "username already exists");

        user.setUsername(uname);
        user.setPassword(passwordEncoder.encode(body.password()));
        user.setRole(Role.STUDENT);

        Student entity = new Student();
        entity.setFullName(body.fullName());
        entity.setSurname(body.surname());
        entity.setEmail(body.email());
        entity.setPhone(body.phone());
        entity.setUser(user);
        entity.setMatricule(body.matricule());
        entity.setPlaceOfBirth(body.placeOfBirth());
        entity.setAddress(body.address());
        entity.setImg(body.img());
        entity.setBloodType(body.bloodType());
        entity.setSex(body.sex());
        entity.setBirthday(body.birthday());

        if (body.parentId() != null) {
            Parent parent = parentRepo.findById(body.parentId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "parent not found"));
            entity.setParent(parent);
        }

        if (body.classId() != null) {
            SchoolClass sc = classRepo.findById(body.classId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "class not found"));
            entity.setSchoolClass(sc);
        }

        return StudentMapper.toDto(studentRepo.save(entity));
    }



    @PutMapping("{id}")
    public StudentDto updateStudent(@PathVariable Long id,
                             @RequestBody StudentDto in) {

        Student entity = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "student not found"));

        StudentMapper.copyOnWrite(in, entity, classRepo, parentRepo);
        return StudentMapper.toDto(studentRepo.save(entity));
    }



    @PutMapping("{id}/class/{targetId}")
    public StudentDto changeClass(@PathVariable Long id,
                                  @PathVariable("targetId") Long newClass) {
        return StudentMapper.toDto(studentService.move(id, newClass));
    }

    @GetMapping("{id}/results")
    public List<ResultDto> resultsForStudent(@PathVariable Long id) {
        findStudent(id);
        List<Result> all = resultRepo.findByStudent_Id(id);
        List<ResultDto> out = new ArrayList<>();
        for (Result r : all) out.add(ResultDto.from(r));
        return out;
    }

    @PostMapping("{id}/results")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createResult(@PathVariable Long id, @RequestBody Result body) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (body.getExam() == null || body.getScore() == null || body.getKind() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exam, score and kind required");
        body.setStudent(student);
        if (body.getIsFinal() == null) body.setIsFinal(false);
        return resultRepo.save(body);
    }

    @PutMapping("{sid}/results/{rid}")
    public ResultDto updateResult(@PathVariable("sid") Long studentId,
                                  @PathVariable("rid") Long resultId,
                                  @RequestBody Result in) {
        Result result = resultRepo.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "result not found"));
        if (!result.getStudent().getId().equals(studentId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "result " + resultId + " not for student " + studentId);
        if (in.getScore() != null) result.setScore(in.getScore());
        if (in.getKind() != null) result.setKind(in.getKind());
        return ResultDto.from(resultRepo.save(result));
    }

    @GetMapping("{id}/attendance")
    public List<AttendanceDto> attendanceForStudent(@PathVariable Long id) {
        findStudent(id);
        List<Attendance> all = attendanceRepo.findByStudentId(id);
        List<AttendanceDto> out = new ArrayList<>();
        for (Attendance a : all) out.add(AttendanceDto.from(a));
        return out;
    }

    @PostMapping("{id}/attendance")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceDto createAttendanceForStudent(@PathVariable Long id, @RequestBody AttendanceDto body) {
        Attendance entity = AttendanceMapper.toEntity(body, studentRepo, lessonRepo);
        // enforce student id from path
        entity.setStudent(studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found")));
        return AttendanceMapper.toDto(attendanceRepo.save(entity));
    }

    @PutMapping("{sid}/attendance/{aid}")
    public AttendanceDto updateAttendanceForStudent(@PathVariable("sid") Long studentId,
                                                    @PathVariable("aid") Long attendanceId,
                                                    @RequestBody AttendanceDto in) {
        Attendance att = attendanceRepo.findById(attendanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "attendance not found"));
        if (!att.getStudent().getId().equals(studentId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "attendance " + attendanceId + " not for student " + studentId);
        AttendanceMapper.copyOnWrite(in, att, studentRepo, lessonRepo);
        return AttendanceMapper.toDto(attendanceRepo.save(att));
    }



    @DeleteMapping("{id}")
    public void removeStudent(@PathVariable Long id) {
        studentRepo.deleteById(id);
    }
}
