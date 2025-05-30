package com.school.controller;

import com.school.model.Attendance;
import com.school.repository.AttendanceRepository;
import com.school.repository.StudentRepository;
import com.school.repository.LessonRepository;
import com.school.dto.AttendanceDto;
import com.school.dto.AttendanceMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.AttendanceService;          
import java.time.LocalDate;                           

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

@RestController
@RequestMapping("/attendances")
public class AttendanceController {

    private final AttendanceRepository attendanceRepo;
    private final AttendanceService    attendanceService;
    private final StudentRepository    studentRepo;
    private final LessonRepository     lessonRepo;

    //only possible statuses are here
    private final Set<String> allowed = Set.of("PRESENT", "ABSENT", "LATE");

    public AttendanceController(AttendanceRepository repo,
                                AttendanceService    service,
                                StudentRepository    students,
                                LessonRepository     lessons) {
        this.attendanceRepo  = repo;
        this.attendanceService = service;
        this.studentRepo      = students;
        this.lessonRepo       = lessons;
   }

    @GetMapping
    public List<AttendanceDto> allAttendances() {
        List<Attendance> all = attendanceRepo.findAll();
        List<AttendanceDto> out = new ArrayList<>();
        for (Attendance attendance : all) {
            out.add(AttendanceDto.from(attendance));
        }
        return out;
    }

    @GetMapping("{id}")
    public AttendanceDto findAttendance(@PathVariable Long id) {
        Attendance attendance = attendanceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "attendance " + id + " not found"));
        return AttendanceDto.from(attendance);
    }

    //  /students/{id}/attendance/percentage 


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceDto createAttendance(@RequestBody Attendance body) {

        if (body.getDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date required");
        if (body.getStatus() == null || !allowed.contains(body.getStatus().toUpperCase()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be PRESENT / ABSENT / LATE");
        if (body.getStudent() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student required");
        if (body.getLesson() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lesson required");

        return AttendanceDto.from(attendanceRepo.save(body));
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Attendance> bulk(@RequestBody List<Attendance> list){
        if(list == null || list.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"empty payload");
        for(Attendance entry : list){
            if(entry.getDate() == null) entry.setDate(LocalDate.now());
            if(entry.getStatus() == null || !allowed.contains(entry.getStatus().toUpperCase()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"status must be PRESENT, ABSENT or LATE");
        }
        return attendanceRepo.saveAll(list);
    }


    @PutMapping("{id}")
    public AttendanceDto updateAttendance(@PathVariable Long id, @RequestBody AttendanceDto in) {

        Attendance attendance = attendanceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "attendance " + id + " not found"));
        AttendanceMapper.copyOnWrite(in, attendance, studentRepo, lessonRepo);
        return AttendanceMapper.toDto(attendanceRepo.save(attendance));
    }

    @DeleteMapping("{id}")
    public void removeAttendance(@PathVariable Long id) {
        attendanceRepo.deleteById(id);
    }
}
