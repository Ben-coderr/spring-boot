package project.edusphere.admin.controller;

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

import project.edusphere.admin.dto.AdminDTO;
import project.edusphere.admin.service.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "APIs for managing administrators")
public class AdminController {

    private final AdminService svc;

    @Operation(summary = "Get all administrators", description = "Retrieves a list of all administrators")
    @ApiResponse(responseCode = "200", description = "List of administrators retrieved successfully")
    @GetMapping
    public List<AdminDTO> getAll() { 
        return svc.findAll(); 
    }

    @Operation(summary = "Get administrator by ID", description = "Retrieves an administrator by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Administrator found",
                content = @Content(schema = @Schema(implementation = AdminDTO.class))),
        @ApiResponse(responseCode = "404", description = "Administrator not found")
    })
    @GetMapping("/{id}")
    public AdminDTO getOne(
            @Parameter(description = "ID of the administrator to retrieve") 
            @PathVariable Long id) { 
        return svc.findById(id); 
    }

    @Operation(summary = "Create a new administrator", description = "Creates a new administrator record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Administrator created successfully")
    })
    @PostMapping
    public ResponseEntity<AdminDTO> create(
            @Parameter(description = "Administrator data to create", 
                      required = true, 
                      schema = @Schema(implementation = AdminDTO.class))
            @RequestBody AdminDTO dto) {
        AdminDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/admins/" + saved.getId())).body(saved);
    }

    @Operation(summary = "Update an administrator", description = "Updates an existing administrator record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Administrator updated successfully"),
        @ApiResponse(responseCode = "404", description = "Administrator not found")
    })
    @PutMapping("/{id}")
    public AdminDTO update(
            @Parameter(description = "ID of the administrator to update") 
            @PathVariable Long id, 
            @Parameter(description = "Updated administrator data", required = true) 
            @RequestBody AdminDTO dto) {
        return svc.update(id, dto);
    }

    @Operation(summary = "Delete an administrator", description = "Deletes an administrator record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Administrator deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Administrator not found")
    })
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "ID of the administrator to delete") 
            @PathVariable Long id) { 
        svc.delete(id); 
    }
}