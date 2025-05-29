// src/main/java/com/school/controller/AuthController.java
package com.school.controller;

import com.school.dto.LoginResponse;
import com.school.model.Role;
import com.school.model.User;
import com.school.repository.*;
import com.school.security.JwtUtil;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
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

    public AuthController(AuthenticationManager authManager,
                          JwtUtil              jwt,
                          TeacherRepository    teachers,
                          ParentRepository     parents,
                          StudentRepository    students,
                          AdminRepository      admins) {
        this.authManager = authManager;
        this.jwt         = jwt;
        this.teachers    = teachers;
        this.parents     = parents;
        this.students    = students;
        this.admins      = admins;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthRequest req) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.username(), req.password()));

        User u = (User) auth.getPrincipal();

        //refuse unapproved accounts
        if (!u.isApproved()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Account not approved");
        }
        

        String token = jwt.generateToken(u);

        return new LoginResponse(
                token,
                u.getRole().name(),
                u.getId(),
                resolveFullName(u)
        );
    }

    /** Pick the “owning” entity and return its fullName, or fallback to username */
    private String resolveFullName(User u) {
        Long uid = u.getId();
        Role r   = u.getRole();

        return switch (r) {
            case TEACHER -> teachers.findByUser_Id(uid)
                                    .map(t -> t.getFullName()).orElse(u.getUsername());
            case PARENT  -> parents .findByUser_Id(uid)
                                    .map(p -> p.getFullName()).orElse(u.getUsername());
            case STUDENT -> students.findByUser_Id(uid)
                                    .map(s -> s.getFullName()).orElse(u.getUsername());
            case ADMIN   -> admins  .findByUser_Id(uid)
                                    .map(a -> a.getFullName()).orElse(u.getUsername());
        };
    }

    /* -------------------------------------------------------------------- */
    private record AuthRequest(String username, String password) {}
}
