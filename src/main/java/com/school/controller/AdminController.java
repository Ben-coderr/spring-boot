package com.school.controller;

import com.school.model.Admin;
import com.school.model.User;
import com.school.repository.AdminRepository;
import com.school.repository.UserRepository;
import com.school.dto.AdminDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminRepository admins;
    private final UserRepository  users;
    public AdminController(AdminRepository repo, UserRepository users){
        this.admins = repo;
        this.users  = users;
    }

    //helper to validate needded informations
    private static void need(String v,String f){
        if(v==null||v.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,f+" required");
    }

    @GetMapping
    public List<AdminDto> list(){
        List<Admin> all = admins.findAll();
        List<AdminDto> out = new ArrayList<>();
        for (Admin admin : all) {
            out.add(AdminDto.from(admin));
        }
        return out;
    }

    @GetMapping("{id}")
    public AdminDto get(@PathVariable Long id){
        Admin admin = admins.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"admin "+id+" not found"));
        return AdminDto.from(admin);
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
        Admin admin = admins.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"admin "+id+" not found"));
        if(in.getFullName()!=null) admin.setFullName(in.getFullName());
        if(in.getEmail()!=null)    admin.setEmail(in.getEmail());
        if(in.getPassword()!=null) admin.setPassword(in.getPassword());
        return AdminDto.from(admins.save(admin));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id){ admins.deleteById(id); }

    @GetMapping("/unapproved")
    public java.util.List<User> unapproved(){
        return users.findByApprovedFalse();
    }

    @PostMapping("/approve/{userId}")
    public void approve(@PathVariable Long userId){
        User user = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));
        user.setApproved(true);
        users.save(user);
    }
}
