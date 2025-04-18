package com.school.system.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {
    @GetMapping(path = "/hello")
    public String helloWorld(){
        return "wee chagotli";
    }
}
