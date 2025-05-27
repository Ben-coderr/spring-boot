package com.school.controller;

import com.school.model.Admin;
import com.school.repository.AdminRepository;
import com.school.dto.AdminDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminRepository admins;
    public AdminController(AdminRepository repo){ this.admins = repo; }

    //helper to validate needded informations
    private static void need(String v,String f){
        if(v==null||v.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,f+" required");
    }

    @GetMapping
    public List<AdminDto> list(){
        return admins.findAll().stream().map(AdminDto::from).toList();
    }

    @GetMapping("{id}")
    public AdminDto get(@PathVariable Long id){
        Admin a = admins.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"admin "+id+" not found"));
        return AdminDto.from(a);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminDto create(@RequestBody Admin body){
        need(body.getFullName(),"name");
        need(body.getEmail(),"email");
        need(body.getPassword(),"password");
        return AdminDto.from(admins.save(body));
    }

    @PutMapping("{id}")
    public AdminDto update(@PathVariable Long id,@RequestBody Admin in){
        Admin a = admins.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"admin "+id+" not found"));
        if(in.getFullName()!=null) a.setFullName(in.getFullName());
        if(in.getEmail()!=null)    a.setEmail(in.getEmail());
        if(in.getPassword()!=null) a.setPassword(in.getPassword());
        return AdminDto.from(admins.save(a));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ admins.deleteById(id); }
}
