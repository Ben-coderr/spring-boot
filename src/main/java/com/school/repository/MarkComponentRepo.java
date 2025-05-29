package com.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.model.MarkComponent;

public interface MarkComponentRepo
    extends JpaRepository<MarkComponent, Long> {}