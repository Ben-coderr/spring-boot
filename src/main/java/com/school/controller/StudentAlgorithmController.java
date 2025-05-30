package com.school.controller;

import com.school.dto.StudentDto;
import com.school.service.StudentAlgorithmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


 //Endpoints that will use the search algorithms implemented.

@RestController
@RequestMapping("/students")
public class StudentAlgorithmController {

    private final StudentAlgorithmService service;

    public StudentAlgorithmController(StudentAlgorithmService service) {
        this.service = service;
    }

    @GetMapping("/sort/{field}")
    public List<StudentDto> sort(@PathVariable String field,
                                 @RequestParam(defaultValue = "bubble") String algo) {
        return service.sort(field, algo);
    }

    @GetMapping("/search/{field}/{value}")
    public StudentDto search(@PathVariable String field, @PathVariable String value) {
        return service.search(field, value);
    }
}
