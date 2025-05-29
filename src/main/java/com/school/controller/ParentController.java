package com.school.controller;

import com.school.model.Parent;
import com.school.model.Role;
import com.school.model.User;
import com.school.repository.ParentRepository;
import com.school.dto.ParentDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/parents")
public class ParentController { // manage parents
    private final ParentRepository parentRepo;
    private final PasswordEncoder  passwordEncoder;

    public ParentController(ParentRepository repo,
                            PasswordEncoder   passwordEncoder) {
        this.parentRepo = repo;
        this.passwordEncoder = passwordEncoder;
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
    public ParentDto registerParent(@RequestBody Parent body){

        must(body.getFullName(),"name");
        must(body.getUser() != null ? body.getUser().getPassword() : null,
            "password");

        // build the user (username = email if present, else phone)
        User user = new User();
        String uname = (body.getEmail() != null && !body.getEmail().isBlank())
                    ? body.getEmail()
                    : body.getPhone();
        user.setUsername(uname);
        user.setPassword(passwordEncoder.encode(body.getUser().getPassword()));
        user.setRole(Role.PARENT);
        body.setUser(user);

        return ParentDto.from(parentRepo.save(body));
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
}
