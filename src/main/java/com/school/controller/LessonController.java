package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.school.exception.ResourceNotFoundException;
import com.school.exception.BadRequestException;
import com.school.exception.ApiException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/lessons")
public class LessonController { // lessons api


    private final LessonRepository lessons;
    private final ExamRepository   examRepo;
    private final AssignmentRepository assignmentRepo;
    private final StudentRepository studentRepo;
    private final AttendanceRepository attendanceRepo;
    public LessonController(LessonRepository repo, ExamRepository examRepo,
                            AssignmentRepository assignmentRepo,
                            StudentRepository studentRepo,
                            AttendanceRepository attendanceRepo){
        this.lessons = repo;
        this.examRepo = examRepo;
        this.assignmentRepo = assignmentRepo;
        this.studentRepo = studentRepo;
        this.attendanceRepo = attendanceRepo;
    }

    @GetMapping
    public List<LessonDto> list(){
        List<Lesson> all = lessons.findAll();
        List<LessonDto> out = new ArrayList<>();
        for (Lesson lesson : all) {
            out.add(LessonDto.from(lesson));
        }
        return out;
    }

    @GetMapping("{id}")
    public LessonDto get(@PathVariable Long id){
        try {
            Lesson lesson = lessons.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("lesson " + id + " not found"));
            return LessonDto.from(lesson);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LessonDto add(@RequestBody Lesson body){
        try {
            if(body.getTopic()==null || body.getTopic().isBlank())
                throw new BadRequestException("topic required");
            if(body.getSubject()==null || body.getTeacher()==null || body.getSchoolClass()==null)
                throw new BadRequestException("subject, teacher and class required");
            return LessonDto.from(lessons.save(body));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @PutMapping("{id}")
    public LessonDto edit(@PathVariable Long id,@RequestBody Lesson in){
        Lesson lesson = lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"lesson "+id+" not found"));
        if(in.getTopic()!=null)       lesson.setTopic(in.getTopic());
        if(in.getLessonDate()!=null)  lesson.setLessonDate(in.getLessonDate());
        if(in.getDay()!=null)         lesson.setDay(in.getDay());
        if(in.getStartTime()!=null)   lesson.setStartTime(in.getStartTime());
        if(in.getEndTime()!=null)     lesson.setEndTime(in.getEndTime());
        if(in.getSubject()!=null)     lesson.setSubject(in.getSubject());
        if(in.getTeacher()!=null)     lesson.setTeacher(in.getTeacher());
        if(in.getSchoolClass()!=null) lesson.setSchoolClass(in.getSchoolClass());
        return LessonDto.from(lessons.save(lesson));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ lessons.deleteById(id); }

    @GetMapping("{id}/exams")
    public List<ExamDto> examsForLesson(@PathVariable Long id) {
        Lesson lesson = lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + id + " not found"));
        List<Exam> all = examRepo.findByLesson_Id(id);
        List<ExamDto> out = new ArrayList<>();
        for (Exam e : all) out.add(ExamDto.from(e));
        return out;
    }

    @PostMapping("{id}/exams")
    @ResponseStatus(HttpStatus.CREATED)
    public ExamDto createExamForLesson(@PathVariable Long id,
                                       @RequestBody CreateExamForLessonReq req) {
        Lesson lesson = lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + id + " not found"));
        Exam exam = new Exam();
        exam.setLesson(lesson);
        exam.setTitle(req.title());
        exam.setExamDate(req.examDate() != null ? req.examDate() : lesson.getLessonDate());
        return ExamDto.from(examRepo.save(exam));
    }

    @PutMapping("{lid}/exams/{eid}")
    public ExamDto updateExamForLesson(@PathVariable("lid") Long lessonId,
                                       @PathVariable("eid") Long examId,
                                       @RequestBody Exam in) {
        try {
            Exam exam = examRepo.findById(examId)
                    .orElseThrow(() -> new ResourceNotFoundException("exam not found"));
            if (exam.getLesson() == null)
                throw new BadRequestException("lesson not set");
            if (!exam.getLesson().getId().equals(lessonId))
                throw new ResourceNotFoundException("exam " + examId + " not for lesson " + lessonId);
            if (in.getTitle() != null) exam.setTitle(in.getTitle());
            if (in.getExamDate() != null) exam.setExamDate(in.getExamDate());
            return ExamDto.from(examRepo.save(exam));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @GetMapping("{id}/assignments")
    public List<AssignmentDto> assignmentsForLesson(@PathVariable Long id) {
        lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + id + " not found"));
        List<Assignment> all = assignmentRepo.findByLesson_Id(id);
        List<AssignmentDto> out = new ArrayList<>();
        for (Assignment a : all) out.add(AssignmentMapper.toDto(a));
        return out;
    }

    @PostMapping("{id}/assignments")
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentDto createAssignmentForLesson(@PathVariable Long id, @RequestBody AssignmentDto body) {
        Lesson lesson = lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + id + " not found"));
        Assignment entity = AssignmentMapper.toEntity(body, lessons);
        entity.setLesson(lesson);
        if (entity.getTitle() == null || entity.getTitle().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title required");
        return AssignmentMapper.toDto(assignmentRepo.save(entity));
    }

    @PutMapping("{lid}/assignments/{aid}")
    public AssignmentDto updateAssignmentForLesson(@PathVariable("lid") Long lessonId,
                                                  @PathVariable("aid") Long aid,
                                                  @RequestBody AssignmentDto in) {
        try {
            Assignment ass = assignmentRepo.findById(aid)
                    .orElseThrow(() -> new ResourceNotFoundException("assignment not found"));
            if (ass.getLesson() == null)
                throw new BadRequestException("lesson not set");
            if (!ass.getLesson().getId().equals(lessonId))
                throw new ResourceNotFoundException("assignment " + aid + " not for lesson " + lessonId);
            AssignmentMapper.copyOnWrite(in, ass, lessons);
            return AssignmentMapper.toDto(assignmentRepo.save(ass));
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    // students for this lesson
    @GetMapping("{id}/students")
    public List<StudentDto> studentsForLesson(@PathVariable Long id) {
        Lesson lesson = lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + id + " not found"));
        Long classId = lesson.getSchoolClass() != null ? lesson.getSchoolClass().getId() : null;
        List<StudentDto> out = new ArrayList<>();
        if (classId != null) {
            for (Student s : studentRepo.findBySchoolClass_Id(classId)) {
                out.add(StudentMapper.toDto(s));
            }
        }
        return out;
    }

    // list attendance rows for lesson
    @GetMapping("{id}/attendance")
    public List<AttendanceDto> attendanceForLesson(@PathVariable Long id) {
        lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + id + " not found"));
        List<Attendance> all = attendanceRepo.findByLesson_Id(id);
        List<AttendanceDto> out = new ArrayList<>();
        for (Attendance a : all) out.add(AttendanceDto.from(a));
        return out;
    }

    // bulk add attendance for a lesson
    @PostMapping("{id}/attendance")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Attendance> createAttendanceBulk(@PathVariable Long id,
                                                 @RequestBody List<Attendance> body) {
        Lesson lesson = lessons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "lesson " + id + " not found"));
        for (Attendance a : body) {
            a.setLesson(lesson);
            if (a.getStudent() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student required");
            if (a.getDate() == null)
                a.setDate(java.time.LocalDate.now());
        }
        return attendanceRepo.saveAll(body);
    }
}
