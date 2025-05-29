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

    public TeacherController(TeacherRepository repo,
                             PasswordEncoder    passwordEncoder,
                             UserRepository      userRepo,
                             SubjectRepository   subjectRepo,
                             LessonRepository    lessonRepo,
                             ExamRepository      examRepo,
                             AssignmentRepository assignmentRepo) {
        this.teacherRepo   = repo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo      = userRepo;
        this.subjectRepo   = subjectRepo;
        this.lessonRepo    = lessonRepo;
        this.examRepo      = examRepo;
        this.assignmentRepo = assignmentRepo;
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
        for (Lesson l : all) out.add(LessonDto.from(l));
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
        for (Exam e : all) out.add(ExamDto.from(e));
        return out;
    }

    @PostMapping("{id}/exams")
    @ResponseStatus(HttpStatus.CREATED)
    public ExamDto createExam(@PathVariable Long id, @RequestBody Exam body) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        if (body.getLesson() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lesson required");
        if (!id.equals(body.getLesson().getTeacher().getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lesson not owned by teacher");
        if (body.getTitle() == null || body.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title required");
        return ExamDto.from(examRepo.save(body));
    }

    @PutMapping("{tid}/exams/{eid}")
    public ExamDto updateExam(@PathVariable("tid") Long teacherId,
                              @PathVariable("eid") Long examId,
                              @RequestBody Exam in) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "exam " + examId + " not found"));
        if (!exam.getLesson().getTeacher().getId().equals(teacherId))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "exam " + examId + " not for teacher " + teacherId);
        if (in.getTitle() != null) exam.setTitle(in.getTitle());
        if (in.getExamDate() != null) exam.setExamDate(in.getExamDate());
        return ExamDto.from(examRepo.save(exam));
    }

    @GetMapping("{id}/assignments")
    public List<AssignmentDto> assignmentsForTeacher(@PathVariable Long id) {
        teacherRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher " + id + " not found"));
        List<Assignment> all = assignmentRepo.findByLesson_Teacher_Id(id);
        List<AssignmentDto> out = new ArrayList<>();
        for (Assignment a : all) out.add(AssignmentMapper.toDto(a));
        return out;
    }

}
