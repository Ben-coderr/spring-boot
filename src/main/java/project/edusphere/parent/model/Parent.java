package project.edusphere.parent.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

import project.edusphere.common.audit.Auditable;
import project.edusphere.common.enums.Role;
import project.edusphere.student.model.Student;
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Parent extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;
// Parent.java
        @Column(nullable = false)
        private String password;
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

