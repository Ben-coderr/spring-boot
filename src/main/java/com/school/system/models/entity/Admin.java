package com.school.system.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

// Admin.java
@Entity
@Table(name = "admins")
@Getter
@Setter
public class Admin extends BaseEntity {
    @Id
    private String id;

    @Column(unique = true)
    private String username;
}
