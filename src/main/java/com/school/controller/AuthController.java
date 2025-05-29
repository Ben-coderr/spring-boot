// src/main/java/com/school/controller/AuthController.java
package com.school.controller;

import com.school.dto.LoginResponse;
import com.school.dto.SignupReq;
import com.school.model.*;
import com.school.repository.*;
import com.school.security.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager  authManager;
    private final JwtUtil                jwt;
    private final TeacherRepository      teachers;
    private final ParentRepository       parents;
    private final StudentRepository      students;
    private final AdminRepository        admins;
    private final PasswordEncoder        encoder;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil              jwt,
                          TeacherRepository    teachers,
                          ParentRepository     parents,
                          StudentRepository    students,
                          AdminRepository      admins,
                          PasswordEncoder      encoder) {
        this.authManager = authManager;
        this.jwt         = jwt;
        this.teachers    = teachers;
        this.parents     = parents;
        this.students    = students;
        this.admins      = admins;
        this.encoder     = encoder;
    }

    private static void need(String value,String field){
        if(value==null||value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,field+" required");
    }

    @PostMapping("/signup/student")
    @ResponseStatus(HttpStatus.CREATED)
    public void signupStudent(@RequestBody SignupReq req){
        need(req.username(),"username");
        need(req.password(),"password");
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));
        user.setRole(Role.STUDENT);
        user.setApproved(false);
        Student student = new Student();
        student.setFullName(req.fullName());
        student.setEmail(req.email());
        student.setPhone(req.phone());
        student.setUser(user);
        students.save(student);
    }

    @PostMapping("/signup/teacher")
    @ResponseStatus(HttpStatus.CREATED)
    public void signupTeacher(@RequestBody SignupReq req){
        need(req.username(),"username");
        need(req.password(),"password");
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));
        user.setRole(Role.TEACHER);
        user.setApproved(false);
        Teacher teacher = new Teacher();
        teacher.setFullName(req.fullName());
        teacher.setEmail(req.email());
        teacher.setPhone(req.phone());
        teacher.setUser(user);
        teachers.save(teacher);
    }

    @PostMapping("/signup/parent")
    @ResponseStatus(HttpStatus.CREATED)
    public void signupParent(@RequestBody SignupReq req){
        need(req.username(),"username");
        need(req.password(),"password");
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));
        user.setRole(Role.PARENT);
        user.setApproved(false);
        Parent parent = new Parent();
        parent.setFullName(req.fullName());
        parent.setEmail(req.email());
        parent.setPhone(req.phone());
        parent.setUser(user);
        parents.save(parent);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthRequest req) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.username(), req.password()));

        User user = (User) auth.getPrincipal();

        //refuse unapproved accounts
        if (!user.isApproved()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account not approved");
        }
        

        String token = jwt.generateToken(user);

        return new LoginResponse(
                token,
                user.getRole().name(),
                user.getId(),
                resolveFullName(user)
        );
    }

    // Pick the "owning" entity and return its fullName, or fallback to username
    private String resolveFullName(User user) {
        Long uid = user.getId();
        Role role   = user.getRole();

        return switch (role) {
            case TEACHER -> teachers.findByUser_Id(uid)
                                    .map(t -> t.getFullName())
                                    .orElse(user.getUsername());
            case PARENT  -> parents.findByUser_Id(uid)
                                    .map(p -> p.getFullName())
                                    .orElse(user.getUsername());
            case STUDENT -> students.findByUser_Id(uid)
                                    .map(s -> s.getFullName())
                                    .orElse(user.getUsername());
            case ADMIN   -> user.getUsername();
        };
    }

    //request payload for login
    private record AuthRequest(String username, String password) {}
}
