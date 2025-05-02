package com.school.parent.controller;

import com.school.parent.dto.ParentDTO;
import com.school.parent.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService svc;

    @GetMapping
    public List<ParentDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public ParentDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<ParentDTO> create(@RequestBody ParentDTO dto) {
        ParentDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/parents/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ParentDTO update(@PathVariable Long id, @RequestBody ParentDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }
}
