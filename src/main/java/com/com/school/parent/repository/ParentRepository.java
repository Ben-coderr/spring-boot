package com.com.school.parent.repository;

import com.com.school.parent.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Long> { }