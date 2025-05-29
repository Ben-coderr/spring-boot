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
public class ParentController {
    private final ParentRepository parents;
    private final PasswordEncoder  encoder;          

    public ParentController(ParentRepository repo,
                            PasswordEncoder   encoder) {
        this.parents = repo;
        this.encoder = encoder;
    }

    private static void must(String v,String f){
        if(v==null||v.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,f+" required");
    }

    @GetMapping
    public List<ParentDto> list(){
        List<Parent> all = parents.findAll();
        List<ParentDto> out = new ArrayList<>();
        for (Parent parent : all) {
            out.add(ParentDto.from(parent));
        }
        return out;
    }

    @GetMapping("{id}")
    public ParentDto get(@PathVariable Long id){
        Parent parent = parents.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"parent "+id+" not found"));
        return ParentDto.from(parent);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParentDto add(@RequestBody Parent body){

        must(body.getFullName(),"name");
        must(body.getUser() != null ? body.getUser().getPassword() : null,
            "password");

        // build the user (username = email if present, else phone)
        User user = new User();
        String uname = (body.getEmail() != null && !body.getEmail().isBlank())
                    ? body.getEmail()
                    : body.getPhone();
        user.setUsername(uname);
        user.setPassword(encoder.encode(body.getUser().getPassword()));
        user.setRole(Role.PARENT);
        body.setUser(user);

        return ParentDto.from(parents.save(body));
    }

    @PutMapping("{id}")
    public ParentDto edit(@PathVariable Long id,@RequestBody Parent in){
        Parent parent = parents.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"parent "+id+" not found"));
        if(in.getFullName()!=null) parent.setFullName(in.getFullName());
        if(in.getEmail()!=null)    parent.setEmail(in.getEmail());
        // if(in.getPassword()!=null) parent.setPassword(in.getPassword());
        return ParentDto.from(parents.save(parent));
    }

    @DeleteMapping("{id}")
    public void remove(@PathVariable Long id){ parents.deleteById(id); }
}
