package project.edusphere.student.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import project.edusphere.student.dto.StudentDTO;
import project.edusphere.student.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    private final StudentService svc;

    @Operation(summary = "Get all students", description = "Retrieves a list of all students")
    @ApiResponse(responseCode = "200", description = "List of students retrieved successfully")
    @GetMapping
    public List<StudentDTO> getAll() { 
        return svc.findAll(); 
    }

    @Operation(summary = "Get student by ID", description = "Retrieves a student by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student found",
                content = @Content(schema = @Schema(implementation = StudentDTO.class))),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/{id}")
    public StudentDTO getOne(
            @Parameter(description = "ID of the student to retrieve") 
            @PathVariable Long id) { 
        return svc.findById(id); 
    }

    @Operation(summary = "Create a new student", description = "Creates a new student record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Student created successfully"),
        @ApiResponse(responseCode = "409", description = "Class capacity exceeded"),
        @ApiResponse(responseCode = "404", description = "Related entity not found")
    })
    @PostMapping
    public ResponseEntity<StudentDTO> create(
            @Parameter(description = "Student data to create", 
                      required = true, 
                      schema = @Schema(implementation = StudentDTO.class))
            @RequestBody StudentDTO dto) {
        StudentDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/students/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Update a student", description = "Updates an existing student record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Student updated successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @PutMapping("/{id}")
    public StudentDTO update(
            @Parameter(description = "ID of the student to update") 
            @PathVariable Long id, 
            @Parameter(description = "Updated student data", required = true) 
            @RequestBody StudentDTO dto) {
        return svc.update(id, dto);
    }

    @Operation(summary = "Delete a student", description = "Deletes a student record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Student deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID of the student to delete") 
            @PathVariable Long id) { 
        svc.delete(id); 
    }
}