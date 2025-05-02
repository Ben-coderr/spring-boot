package com.school.parent.repository;

import com.school.parent.model.Parent;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Long> { 
    Optional<Parent> findByEmail(String email); 
}