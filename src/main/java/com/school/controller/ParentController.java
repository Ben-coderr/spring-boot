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
        return parents.findAll().stream().map(ParentDto::from).toList();
    }

    @GetMapping("{id}")
    public ParentDto get(@PathVariable Long id){
        Parent p = parents.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"parent "+id+" not found"));
        return ParentDto.from(p);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParentDto add(@RequestBody Parent body){

        must(body.getFullName(),"name");
        must(body.getUser() != null ? body.getUser().getPassword() : null,
            "password");

        // build the user (username = email if present, else phone)
        User u = new User();
        String uname = (body.getEmail() != null && !body.getEmail().isBlank())
                    ? body.getEmail()
                    : body.getPhone();
        u.setUsername(uname);
        u.setPassword(encoder.encode(body.getUser().getPassword()));
        u.setRole(Role.PARENT);
        body.setUser(u);

        return ParentDto.from(parents.save(body));
    }

    @PutMapping("{id}")
    public ParentDto edit(@PathVariable Long id,@RequestBody Parent in){
        Parent p = parents.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"parent "+id+" not found"));
        if(in.getFullName()!=null) p.setFullName(in.getFullName());
        if(in.getEmail()!=null)    p.setEmail(in.getEmail());
        // if(in.getPassword()!=null) p.setPassword(in.getPassword());
        return ParentDto.from(parents.save(p));
    }

    @DeleteMapping("{id}")
    public void remove(@PathVariable Long id){ parents.deleteById(id); }
}
