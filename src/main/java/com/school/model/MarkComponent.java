package com.school.model;

import jakarta.persistence.*;

@Entity
public class MarkComponent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private SubjectGradeScheme scheme;

    @Column(nullable = false) private String  kind;
    @Column(nullable = false) private double  weight;   // % of subject average

    /* ---------- getters ---------- */
    public Long   getId()     { return id; }
    public double getWeight() { return weight; }
    public String getKind()   { return kind; }

    /* ---------- setters ---------- */
    public void setScheme(SubjectGradeScheme s) { this.scheme = s; }
    public void setKind(String k)               { this.kind   = k; }
    public void setWeight(double w)             { this.weight = w; }
}
