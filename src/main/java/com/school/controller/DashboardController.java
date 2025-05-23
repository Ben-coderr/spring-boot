package com.school.controller;

import com.school.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//api call for dashboard
@RestController
@RequestMapping("/dash")
public class DashboardController {

    private final DashboardService boards;

    public DashboardController(DashboardService svc) {
        this.boards = svc;
    }


    @GetMapping("/occupancy")
    public List<DashboardService.Occupancy> occupancy() {
        return boards.snapshot();
    }
}
