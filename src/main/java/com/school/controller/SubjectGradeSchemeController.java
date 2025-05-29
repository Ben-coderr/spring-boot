// src/main/java/com/school/controller/SubjectGradeSchemeController.java
package com.school.controller;

import com.school.model.SubjectGradeScheme;
import com.school.repository.SubjectGradeSchemeRepo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schemes")
public class SubjectGradeSchemeController {

    private final SubjectGradeSchemeRepo repo;
    public SubjectGradeSchemeController(SubjectGradeSchemeRepo repository){ this.repo = repository; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public SubjectGradeScheme create(@RequestBody SubjectGradeScheme scheme){
        return repo.save(scheme);
    }
}
