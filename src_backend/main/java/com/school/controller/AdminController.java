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

    private final AdminRepository adminRepo;
    private final UserRepository  userRepo;
    public AdminController(AdminRepository repo, UserRepository userRepo){
        this.adminRepo = repo;
        this.userRepo  = userRepo;
    }

    //helper to validate data
    private static void need(String value,String field){
        if(value==null||value.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,field+" required");
    }

    @GetMapping
    public List<AdminDto> allAdmins(){
        List<Admin> all = adminRepo.findAll();
        List<AdminDto> out = new ArrayList<>();
        for (Admin admin : all) {
            out.add(AdminDto.from(admin));
        }
        return out;
    }

    @GetMapping("{id}")
    public AdminDto findAdmin(@PathVariable Long id){
        Admin admin = adminRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"admin "+id+" not found"));
        return AdminDto.from(admin);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminDto createAdmin(@RequestBody Admin body){
        need(body.getFullName(),"name");
        need(body.getEmail(),"email");
        need(body.getPassword(),"password");
        return AdminDto.from(adminRepo.save(body));
    }

    @PutMapping("{id}")
    public AdminDto updateAdmin(@PathVariable Long id,@RequestBody Admin in){
        Admin admin = adminRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,"admin "+id+" not found"));
        if(in.getFullName()!=null) admin.setFullName(in.getFullName());
        if(in.getEmail()!=null)    admin.setEmail(in.getEmail());
        if(in.getPassword()!=null) admin.setPassword(in.getPassword());
        return AdminDto.from(adminRepo.save(admin));
    }

    @DeleteMapping("{id}")
    public void removeAdmin(@PathVariable Long id){ adminRepo.deleteById(id); }

    @GetMapping("/unapproved")
    public java.util.List<User> unapproved(){
        return userRepo.findByApprovedFalse();
    }

    @PostMapping("/approve/{userId}")
    public void approve(@PathVariable Long userId){
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));
        user.setApproved(true);
        userRepo.save(user);
    }
}
