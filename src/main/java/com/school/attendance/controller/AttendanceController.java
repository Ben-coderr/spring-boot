package com.school.attendance.controller;

import com.school.attendance.dto.AttendanceDTO;
import com.school.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService svc;

    @GetMapping
    public List<AttendanceDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public AttendanceDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<AttendanceDTO> create(@RequestBody AttendanceDTO dto) {
        AttendanceDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/attendances/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public AttendanceDTO update(@PathVariable Long id, @RequestBody AttendanceDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }
}
