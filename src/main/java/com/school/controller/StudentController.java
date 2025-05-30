package com.school.controller;

import com.school.dto.*;
import com.school.model.Role;
import com.school.model.Student;
import com.school.model.User;
import com.school.model.Result;
import com.school.model.Attendance;
import com.school.model.Exam;
import com.school.repository.*;
import com.school.service.AttendanceService;
import com.school.service.StudentService;
import com.school.service.BulletinService;
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
    private final ExamRepository        examRepo;
    private final AttendanceRepository  attendanceRepo;
    private final LessonRepository      lessonRepo;
    private final AssignmentRepository  assignmentRepo;
    private final BulletinService       bulletinService;

    public StudentController(
        StudentRepository     studentRepo,
        SchoolClassRepository classRepo,
        StudentService        service,
        AttendanceService     attendance,
        PasswordEncoder       passwordEncoder,
        UserRepository        userRepo,
        ParentRepository      parentRepo,
        ResultRepository      resultRepo,
        ExamRepository        examRepo,
        AttendanceRepository  attendanceRepo,
        LessonRepository      lessonRepo,
        AssignmentRepository  assignmentRepo,
        BulletinService       bulletinService
    ) {
        this.studentRepo        = studentRepo;
        this.classRepo          = classRepo;
        this.studentService     = service;
        this.attendanceService  = attendance;
        this.passwordEncoder    = passwordEncoder;
        this.userRepo           = userRepo;
        this.parentRepo         = parentRepo;
        this.resultRepo         = resultRepo;
        this.examRepo          = examRepo;
        this.attendanceRepo     = attendanceRepo;
        this.lessonRepo         = lessonRepo;
        this.assignmentRepo     = assignmentRepo;
        this.bulletinService    = bulletinService;
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
        for (Result resultEntity : all) {
            out.add(ResultDto.from(resultEntity));
        }
        return out;
    }

    @PostMapping("{id}/results")
    @ResponseStatus(HttpStatus.CREATED)
    public Result createResult(@PathVariable Long id, @RequestBody ResultDto dto) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (dto.examId() == null || dto.score() == null || dto.kind() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "examId, score and kind required");
        Exam exam = examRepo.findById(dto.examId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exam not found"));

        Result r = new Result();
        r.setStudent(student);
        r.setExam(exam);
        r.setScore(dto.score());
        r.setKind(dto.kind());
        r.setIsFinal(Boolean.FALSE);
        return resultRepo.save(r);
    }

    @PutMapping("{sid}/results/{rid}")
    public ResultDto updateResult(@PathVariable("sid") Long studentId,
                                  @PathVariable("rid") Long resultId,
                                  @RequestBody Result in) {
        Result result = resultRepo.findById(resultId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "result not found"));
        if (result.getStudent() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student not set");
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
        for (Attendance record : all) {
            out.add(AttendanceDto.from(record));
        }
        return out;
    }

    @PostMapping("{id}/attendance")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceDto createAttendanceForStudent(@PathVariable Long id, @RequestBody AttendanceDto body) {
        Attendance entity = new Attendance();
        entity.setStatus(body.status());
        entity.setDate(body.date());
        entity.setLesson(lessonRepo.findById(body.lessonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson not found")));
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
        if (att.getStudent() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student not set");
        if (!att.getStudent().getId().equals(studentId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "attendance " + attendanceId + " not for student " + studentId);
        AttendanceMapper.copyOnWrite(in, att, studentRepo, lessonRepo);
        return AttendanceMapper.toDto(attendanceRepo.save(att));
    }

    // lessons timetable for a student
    @GetMapping("{id}/lessons")
    public List<LessonDto> lessonsForStudent(@PathVariable Long id) {
        Student s = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (s.getSchoolClass() == null) return new ArrayList<>();
        List<Lesson> all = lessonRepo.findBySchoolClass_Id(s.getSchoolClass().getId());
        List<LessonDto> out = new ArrayList<>();
        for (Lesson l : all) out.add(LessonDto.from(l));
        return out;
    }

    // exams for a student
    @GetMapping("{id}/exams")
    public List<ExamDto> examsForStudent(@PathVariable Long id) {
        Student s = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (s.getSchoolClass() == null) return new ArrayList<>();
        List<Exam> all = examRepo.findByLesson_SchoolClass_Id(s.getSchoolClass().getId());
        List<ExamDto> out = new ArrayList<>();
        for (Exam e : all) out.add(ExamDto.from(e));
        return out;
    }

    // assignments for a student
    @GetMapping("{id}/assignments")
    public List<AssignmentDto> assignmentsForStudent(@PathVariable Long id) {
        Student s = studentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (s.getSchoolClass() == null) return new ArrayList<>();
        List<Assignment> all = assignmentRepo.findByLesson_SchoolClass_Id(s.getSchoolClass().getId());
        List<AssignmentDto> out = new ArrayList<>();
        for (Assignment a : all) out.add(AssignmentMapper.toDto(a));
        return out;
    }

    // bulletin shortcut
    @GetMapping("{id}/bulletin")
    public Map<String,Object> bulletin(@PathVariable Long id) {
        return bulletinService.generate(id);
    }

    // raw attendance records
    @GetMapping("{id}/attendance/daily")
    public List<AttendanceDto> daily(@PathVariable Long id) {
        return attendanceForStudent(id);
    }



    @DeleteMapping("{id}")
    public void removeStudent(@PathVariable Long id) {
        studentRepo.deleteById(id);
    }
}
