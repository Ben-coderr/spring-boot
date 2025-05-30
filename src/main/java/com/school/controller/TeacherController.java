package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/teachers")
public class TeacherController { // teacher endpoints

    private final TeacherRepository   teacherRepo;
    private final PasswordEncoder     passwordEncoder;
    private final UserRepository      userRepo;
    private final SubjectRepository   subjectRepo;
    private final LessonRepository    lessonRepo;
    private final ExamRepository      examRepo;
    private final AssignmentRepository assignmentRepo;
    private final StudentRepository   studentRepo;
    private final AttendanceRepository attendanceRepo;
    private final ResultRepository    resultRepo;

    public TeacherController(TeacherRepository repo,
                             PasswordEncoder    passwordEncoder,
                             UserRepository      userRepo,
                             SubjectRepository   subjectRepo,
                             LessonRepository    lessonRepo,
                             ExamRepository      examRepo,
                             AssignmentRepository assignmentRepo,
                             StudentRepository   studentRepo,
                             AttendanceRepository attendanceRepo,
                             ResultRepository    resultRepo) {
        this.teacherRepo   = repo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo      = userRepo;
        this.subjectRepo   = subjectRepo;
        this.lessonRepo    = lessonRepo;
        this.examRepo      = examRepo;
        this.assignmentRepo = assignmentRepo;
        this.studentRepo   = studentRepo;
        this.attendanceRepo = attendanceRepo;
        this.resultRepo    = resultRepo;
    }

    //helper to validate needed fields
    private static void need(String value, String field) {
        if (value == null || value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " required");
    }


    @GetMapping
    public List<TeacherDto> listTeachers() {
        List<Teacher> all = teacherRepo.findAll();
        List<TeacherDto> out = new ArrayList<>();
        for (Teacher teacher : all) {
            out.add(TeacherDto.from(teacher));
        }
        return out;
    }

    @GetMapping("{id}")
    public TeacherDto getTeacher(@PathVariable Long id) {
        Teacher teacher = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        return TeacherDto.from(teacher);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherDto registerTeacher(@RequestBody TeacherReq body){

        need(body.fullName(),"name");
        need(body.password(),"password");

        User user = new User();
        String uname = (body.email() != null && !body.email().isBlank())
                    ? body.email()
                    : body.phone();

        if (userRepo.findByUsername(uname).isPresent())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "username already exists");

        user.setUsername(uname);
        user.setPassword(passwordEncoder.encode(body.password()));
        user.setRole(Role.TEACHER);

        Teacher entity = new Teacher();
        entity.setFullName(body.fullName());
        entity.setEmail(body.email());
        entity.setPhone(body.phone());
        entity.setPlaceOfBirth(body.placeOfBirth());
        entity.setUser(user);
        entity.setImg(body.img());
        entity.setBloodType(body.bloodType());
        entity.setSex(body.sex());
        entity.setBirthday(body.birthday());

        if (body.subjectId() != null) {
            Subject subject = subjectRepo.findById(body.subjectId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "subject not found"));
            entity.setSubject(subject);
        }

        return TeacherDto.from(teacherRepo.save(entity));
    }

    @PutMapping("{id}")
    public TeacherDto updateTeacher(@PathVariable Long id, @RequestBody TeacherDto in) {

        Teacher teacher = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));

        TeacherMapper.copyOnWrite(in, teacher, subjectRepo);
        return TeacherMapper.toDto(teacherRepo.save(teacher));
    }

    @DeleteMapping("{id}")
    public void removeTeacher(@PathVariable Long id) {
        teacherRepo.deleteById(id);
    }

    @GetMapping("{id}/lessons")
    public List<LessonDto> lessonsForTeacher(@PathVariable Long id) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        List<Lesson> all = lessonRepo.findByTeacher_Id(id);
        List<LessonDto> out = new ArrayList<>();
        for (Lesson lesson : all) {
            out.add(LessonDto.from(lesson));
        }
        return out;
    }

    @PostMapping("{id}/lessons")
    @ResponseStatus(HttpStatus.CREATED)
    public LessonDto createLesson(@PathVariable Long id, @RequestBody Lesson body) {
        Teacher teacher = teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        body.setTeacher(teacher);
        if (body.getTopic() == null || body.getTopic().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "topic required");
        if (body.getSubject() == null || body.getSchoolClass() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "subject and class required");
        return LessonDto.from(lessonRepo.save(body));
    }

    @PutMapping("{tid}/lessons/{lid}")
    public LessonDto updateLesson(@PathVariable("tid") Long teacherId,
                                  @PathVariable("lid") Long lessonId,
                                  @RequestBody Lesson in) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + lessonId + " not found"));
        if (lesson.getTeacher() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teacher not set");
        if (!lesson.getTeacher().getId().equals(teacherId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + lessonId + " not for teacher " + teacherId);
        if (in.getTopic() != null) lesson.setTopic(in.getTopic());
        if (in.getLessonDate() != null) lesson.setLessonDate(in.getLessonDate());
        if (in.getDay() != null) lesson.setDay(in.getDay());
        if (in.getStartTime() != null) lesson.setStartTime(in.getStartTime());
        if (in.getEndTime() != null) lesson.setEndTime(in.getEndTime());
        if (in.getSubject() != null) lesson.setSubject(in.getSubject());
        if (in.getSchoolClass() != null) lesson.setSchoolClass(in.getSchoolClass());
        return LessonDto.from(lessonRepo.save(lesson));
    }

    @GetMapping("{id}/exams")
    public List<ExamDto> examsForTeacher(@PathVariable Long id) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        List<Exam> all = examRepo.findByLesson_Teacher_Id(id);
        List<ExamDto> out = new ArrayList<>();
        for (Exam exam : all) {
            out.add(ExamDto.from(exam));
        }
        return out;
    }

    @PostMapping("{id}/exams")
    @ResponseStatus(HttpStatus.CREATED)
    public ExamDto createExam(@PathVariable Long id,
                              @RequestBody CreateExamReq req) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        Lesson lesson = lessonRepo.findById(req.lessonId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson not found"));
        if (lesson.getTeacher() == null || !lesson.getTeacher().getId().equals(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lesson not owned by teacher");

        Exam exam = new Exam();
        exam.setTitle(req.title());
        exam.setLesson(lesson);
        exam.setExamDate(req.examDate() != null ? req.examDate() : lesson.getLessonDate());
        return ExamDto.from(examRepo.save(exam));
    }

    @PutMapping("{tid}/exams/{eid}")
    public ExamDto updateExam(@PathVariable("tid") Long teacherId,
                              @PathVariable("eid") Long examId,
                              @RequestBody ExamDto in) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exam " + examId + " not found"));
        if (exam.getLesson() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lesson not set");
        if (exam.getLesson().getTeacher() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teacher not set");
        if (!exam.getLesson().getTeacher().getId().equals(teacherId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "exam " + examId + " not for teacher " + teacherId);
        if (in.title() != null)       exam.setTitle(in.title());
        if (in.examDate() != null)    exam.setExamDate(in.examDate());
        return ExamDto.from(examRepo.save(exam));
    }

    @GetMapping("{id}/assignments")
    public List<AssignmentDto> assignmentsForTeacher(@PathVariable Long id) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        List<Assignment> all = assignmentRepo.findByLesson_Teacher_Id(id);
        List<AssignmentDto> out = new ArrayList<>();
        for (Assignment assignment : all) {
            out.add(AssignmentMapper.toDto(assignment));
        }
        return out;
    }

    // results for exams given by teacher
    @GetMapping("{id}/exams/results")
    public List<ResultDto> resultsForTeacher(@PathVariable Long id) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        List<Result> all = resultRepo.findByExam_Lesson_Teacher_Id(id);
        List<ResultDto> out = new ArrayList<>();
        for (Result r : all) out.add(ResultDto.from(r));
        return out;
    }

    // raw attendance for students in teacher lessons
    @GetMapping("{id}/students/attendance")
    public List<AttendanceDto> attendanceForTeacher(@PathVariable Long id) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        List<Attendance> all = attendanceRepo.findByLesson_Teacher_Id(id);
        List<AttendanceDto> out = new ArrayList<>();
        for (Attendance a : all) out.add(AttendanceDto.from(a));
        return out;
    }

    // students taught by teacher
    @GetMapping("{id}/students")
    public List<StudentDto> studentsForTeacher(@PathVariable Long id) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        java.util.Set<Long> classIds = new java.util.HashSet<>();
        for (Lesson l : lessonRepo.findByTeacher_Id(id)) {
            if (l.getSchoolClass() != null) classIds.add(l.getSchoolClass().getId());
        }
        List<StudentDto> out = new ArrayList<>();
        for (Long cid : classIds) {
            for (Student s : studentRepo.findBySchoolClass_Id(cid)) {
                out.add(StudentMapper.toDto(s));
            }
        }
        return out;
    }

    // classes taught by teacher
    @GetMapping("{id}/students/classes")
    public List<SchoolClassDto> classesForTeacher(@PathVariable Long id) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        java.util.Set<SchoolClass> classes = new java.util.HashSet<>();
        for (Lesson l : lessonRepo.findByTeacher_Id(id)) {
            if (l.getSchoolClass() != null) classes.add(l.getSchoolClass());
        }
        List<SchoolClassDto> out = new ArrayList<>();
        for (SchoolClass sc : classes) {
            out.add(new SchoolClassDto(sc.getId(), sc.getName(),
                    sc.getGrade() != null ? sc.getGrade().getId() : null));
        }
        return out;
    }

}
