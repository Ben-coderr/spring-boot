package com.school.controller;

import com.school.service.BulletinService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/bulletins")
public class BulletinController { // build bulletins

    private final BulletinService bulletinService;

    public BulletinController(BulletinService service){
        this.bulletinService = service;
    }

    //GET /bulletins/student/id 
    @GetMapping("/student/{id}")
    public Map<String,Object> generateBulletin(@PathVariable Long id){
        return bulletinService.generate(id);
    }
}
