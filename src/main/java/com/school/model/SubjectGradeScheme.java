package com.school.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SubjectGradeScheme {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) private Subject subject;
    @ManyToOne(fetch = FetchType.LAZY) private Grade   grade;

    private Integer coefficient = 1;

    @OneToMany(mappedBy = "scheme",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<MarkComponent> components = new ArrayList<>();

    /* ---------- getters ---------- */
    public Long              getId()          { return id; }
    public Subject           getSubject()     { return subject; }
    public Grade             getGrade()       { return grade; }
    public Integer           getCoefficient() { return coefficient; }
    public List<MarkComponent> getComponents(){ return components; }

    /* ---------- setters ---------- */
    //set subject
    public void setSubject(Subject subject)          { this.subject = subject; }
    //set grade
    public void setGrade(Grade grade)              { this.grade   = grade; }
    //set coefficient value
    public void setCoefficient(Integer coefficient)      { this.coefficient = coefficient; }
}
