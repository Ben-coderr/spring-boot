package com.school.controller;

import com.school.model.Attendance;
import com.school.repository.AttendanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.AttendanceService;          
import java.time.LocalDate;                           

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/attendances")
public class AttendanceController {

    private final AttendanceRepository attendances;
    private final AttendanceService    stats;   

    //only possible statuses are here
    private final Set<String> allowed = Set.of("PRESENT", "ABSENT", "LATE");

    public AttendanceController(AttendanceRepository repo,
                                AttendanceService    svc) {
        this.attendances = repo;
        this.stats       = svc;
   }

    @GetMapping
    public List<Attendance> listAttendances() {
        return attendances.findAll();
    }

    @GetMapping("{id}")
    public Attendance getAttendance(@PathVariable Long id) {
        return attendances.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "attendance " + id + " not found"));
    }

    @GetMapping("/student/{sid}/percent")
    public java.util.Map<String,Object> percent(@PathVariable("sid") Long studentId){
        return stats.percentage(studentId);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attendance createAttendance(@RequestBody Attendance body) {

        if (body.getDate() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "date required");
        //Validation of status
        if (!allowed.contains(body.getStatus()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be PRESENT / ABSENT / LATE");

        if (body.getStudent() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "student required");

        if (body.getLesson() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lesson required");

        return attendances.save(body);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public java.util.List<Attendance> bulk(@RequestBody java.util.List<Attendance> list){

        if(list==null || list.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"empty payload");

        for(Attendance a: list){

            if(a.getDate()==null) a.setDate(LocalDate.now());

            if(!allowed.contains(a.getStatus()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"bad status");

            if(a.getStudent()==null || a.getLesson()==null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"student & lesson needed");
        }
        return attendances.saveAll(list);
    }

    @PutMapping("{id}")
    public Attendance updateAttendance(@PathVariable Long id, @RequestBody Attendance in) {

        Attendance a = getAttendance(id);

        if (in.getStatus() != null) {
            if (!allowed.contains(in.getStatus()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid status");
            a.setStatus(in.getStatus());
        }

        if (in.getDate() != null) a.setDate(in.getDate());
        if (in.getStudent() != null) a.setStudent(in.getStudent());
        if (in.getLesson()  != null) a.setLesson(in.getLesson());

        return attendances.save(a);
    }

    @DeleteMapping("{id}")
    public void deleteAttendance(@PathVariable Long id) {
        attendances.deleteById(id);
    }
}
