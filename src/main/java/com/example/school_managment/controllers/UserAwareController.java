package com.example.school_managment.controllers;

import com.example.school_managment.models.User;

public interface UserAwareController {
    void setCurrentUser(User user);
}