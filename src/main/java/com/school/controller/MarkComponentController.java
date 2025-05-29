// src/main/java/com/school/controller/MarkComponentController.java
package com.school.controller;

import com.school.model.MarkComponent;
import com.school.repository.MarkComponentRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/components")
public class MarkComponentController {

    private final MarkComponentRepo repo;
    public MarkComponentController(MarkComponentRepo repository){ this.repo = repository; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public MarkComponent create(@RequestBody MarkComponent component){
        return repo.save(component);
    }
}
