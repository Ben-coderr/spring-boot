package com.school.controller;

import com.school.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//api call for dashboard
@RestController
@RequestMapping("/dash")
public class DashboardController { // dashboard info

    private final DashboardService boards;

    public DashboardController(DashboardService service) {
        this.boards = service;
    }


    @GetMapping("/occupancy")
    public List<DashboardService.Occupancy> occupancy() {
        return boards.snapshot();
    }
}
