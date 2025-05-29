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
    //set grade scheme
    public void setScheme(SubjectGradeScheme scheme) { this.scheme = scheme; }
    //set component kind
    public void setKind(String kind)               { this.kind   = kind; }
    //set component weight
    public void setWeight(double weight)             { this.weight = weight; }
}
