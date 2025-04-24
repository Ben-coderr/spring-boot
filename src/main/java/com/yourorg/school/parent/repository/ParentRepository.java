package com.yourorg.school.parent.repository;

import com.yourorg.school.parent.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Long> { }