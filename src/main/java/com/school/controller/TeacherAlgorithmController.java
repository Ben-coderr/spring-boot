package com.school.controller;

import com.school.dto.TeacherDto;
import com.school.service.TeacherAlgorithmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Simple sort and search endpoints for teachers demonstrating that the
 * algorithm logic is not limited to students.
 */
@RestController
@RequestMapping("/teachers")
public class TeacherAlgorithmController {

    private final TeacherAlgorithmService service;

    public TeacherAlgorithmController(TeacherAlgorithmService service) {
        this.service = service;
    }

    @GetMapping("/sort/{field}")
    public List<TeacherDto> sort(@PathVariable String field,
                                 @RequestParam(defaultValue = "bubble") String algo) {
        return service.sort(field, algo);
    }

    @GetMapping("/search/{field}/{value}")
    public TeacherDto search(@PathVariable String field, @PathVariable String value) {
        return service.search(field, value);
    }
}
