package com.school.result.controller;

import com.school.result.dto.ResultDTO;
import com.school.result.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService svc;

    @GetMapping
    public List<ResultDTO> getAll() { return svc.findAll(); }

    @GetMapping("/{id}")
    public ResultDTO getOne(@PathVariable Long id) { return svc.findById(id); }

    @PostMapping
    public ResponseEntity<ResultDTO> create(@RequestBody ResultDTO dto) {
        ResultDTO saved = svc.create(dto);
        return ResponseEntity.created(URI.create("/api/results/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResultDTO update(@PathVariable Long id, @RequestBody ResultDTO dto) {
        return svc.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { svc.delete(id); }
}
