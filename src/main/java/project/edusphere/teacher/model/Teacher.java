package project.edusphere.teacher.model;
import project.edusphere.subject.model.Subject;

import java.util.HashSet;
import java.util.Set;

import project.edusphere.common.audit.Auditable;
import project.edusphere.common.enums.Role;
import project.edusphere.schoolclass.model.SchoolClass;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Teacher extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private Role role = Role.TEACHER;

    @ManyToMany
    @JoinTable(name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id"))
    @Builder.Default
    private Set<Subject> subjects = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "teacher_class",
        joinColumns        = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "class_id"))
    private Set<SchoolClass> classes = new HashSet<>();

}
