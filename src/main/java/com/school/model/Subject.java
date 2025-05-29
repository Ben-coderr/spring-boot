package com.school.model;
import jakarta.persistence.*;

@Entity
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer coefficient = 1;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCoefficient() { return coefficient; }
    //set coefficient of subject
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }
}
