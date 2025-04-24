package com.yourorg.school.admin.repository;

import com.yourorg.school.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> { }
