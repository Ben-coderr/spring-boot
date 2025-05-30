package com.school.controller;

import com.school.model.*;
import com.school.repository.*;
import com.school.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.school.service.AttendanceService;
import com.school.service.BulletinService;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/parents")
public class ParentController { // manage parents
    private final ParentRepository parentRepo;
    private final PasswordEncoder  passwordEncoder;
    private final UserRepository   userRepo;
    private final StudentRepository studentRepo;
    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepo;
    private final ResultRepository resultRepo;
    private final BulletinService bulletinService;

    public ParentController(ParentRepository repo,
                            PasswordEncoder   passwordEncoder,
                            UserRepository    userRepo,
                            StudentRepository studentRepo,
                            AttendanceService attendanceService,
                            AttendanceRepository attendanceRepo,
                            ResultRepository resultRepo,
                            BulletinService bulletinService) {
        this.parentRepo = repo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo   = userRepo;
        this.studentRepo = studentRepo;
        this.attendanceService = attendanceService;
        this.attendanceRepo = attendanceRepo;
        this.resultRepo = resultRepo;
        this.bulletinService = bulletinService;
    }

    private static void must(String value,String field){
        if(value==null||value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,field+" required");
    }

    @GetMapping
    public List<ParentDto> allParents(){
        List<Parent> all = parentRepo.findAll();
        List<ParentDto> out = new ArrayList<>();
        for (Parent parent : all) {
            out.add(ParentDto.from(parent));
        }
        return out;
    }

    @GetMapping("{id}")
    public ParentDto findParent(@PathVariable Long id){
        Parent parent = parentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"parent "+id+" not found"));
        return ParentDto.from(parent);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParentDto registerParent(@RequestBody ParentReq body){

        must(body.fullName(),"name");
        must(body.password(),"password");

        User user = new User();
        String uname = (body.email() != null && !body.email().isBlank())
                    ? body.email()
                    : body.phone();

        if (userRepo.findByUsername(uname).isPresent())
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "username already exists");

        user.setUsername(uname);
        user.setPassword(passwordEncoder.encode(body.password()));
        user.setRole(Role.PARENT);

        Parent entity = new Parent();
        entity.setFullName(body.fullName());
        entity.setPhone(body.phone());
        entity.setEmail(body.email());
        entity.setAddress(body.address());
        entity.setUser(user);

        return ParentDto.from(parentRepo.save(entity));
    }

    @PutMapping("{id}")
    public ParentDto updateParent(@PathVariable Long id,@RequestBody Parent in){
        Parent parent = parentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"parent "+id+" not found"));
        if(in.getFullName()!=null) parent.setFullName(in.getFullName());
        if(in.getEmail()!=null)    parent.setEmail(in.getEmail());
        // if(in.getPassword()!=null) parent.setPassword(in.getPassword());
        return ParentDto.from(parentRepo.save(parent));
    }

    @DeleteMapping("{id}")
    public void removeParent(@PathVariable Long id){ parentRepo.deleteById(id); }

    @GetMapping("{id}/students")
    public List<StudentDto> children(@PathVariable Long id) {
        parentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "parent " + id + " not found"));
        List<Student> kids = studentRepo.findByParent_Id(id);
        List<StudentDto> out = new ArrayList<>();
        for (Student s : kids) out.add(StudentMapper.toDto(s));
        return out;
    }

    // attendance percentage per child
    @GetMapping("{id}/attendance/percentage")
    public List<Map<String,Object>> attendancePct(@PathVariable Long id) {
        List<Map<String,Object>> out = new ArrayList<>();
        for (Student s : studentRepo.findByParent_Id(id)) {
            out.add(attendanceService.percentage(s.getId()));
        }
        return out;
    }

    // raw attendance for one child
    @GetMapping("{id}/students/{sid}/attendance")
    public List<AttendanceDto> attendanceChild(@PathVariable Long id,
                                               @PathVariable("sid") Long studentId) {
        Student s = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
        if (s.getParent() == null || !s.getParent().getId().equals(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "student not linked to parent");
        List<AttendanceDto> out = new ArrayList<>();
        for (Attendance a : attendanceRepo.findByStudentId(studentId))
            out.add(AttendanceDto.from(a));
        return out;
    }

    // results for all children
    @GetMapping("{id}/students/results")
    public List<ResultDto> resultsForKids(@PathVariable Long id) {
        List<ResultDto> out = new ArrayList<>();
        for (Student s : studentRepo.findByParent_Id(id)) {
            for (Result r : resultRepo.findByStudent_Id(s.getId()))
                out.add(ResultDto.from(r));
        }
        return out;
    }

    // bulletins for all children
    @GetMapping("{id}/students/bulletin")
    public List<Map<String,Object>> bulletins(@PathVariable Long id) {
        List<Map<String,Object>> out = new ArrayList<>();
        for (Student s : studentRepo.findByParent_Id(id)) {
            out.add(bulletinService.generate(s.getId()));
        }
        return out;
    }
}
