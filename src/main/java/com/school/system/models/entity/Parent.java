package com.school.system.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

// Parent.java
@Entity
@Table(name = "parents")
@Getter
@Setter
public class Parent extends BaseEntity {
    @Id
    private String id;

    @Column(unique = true)
    private String username;

    private String name;
    private String surname;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String address;

    @OneToMany(mappedBy = "parent")
    private List<Student> students = new ArrayList<>();
}
