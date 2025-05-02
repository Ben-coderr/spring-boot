package com.school.parent.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import com.school.common.audit.Auditable;
import com.school.common.enums.Role;
import com.school.student.model.Student;
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Parent extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;
// Parent.java
        private String password;                  // nullable
        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private Role role = Role.PARENT;

    @Column(nullable = false, unique = true)
    private String email;
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "parent_student",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private Set<Student> children = new HashSet<>();
}
