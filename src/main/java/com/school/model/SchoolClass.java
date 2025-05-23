package com.school.model;

import jakarta.persistence.*;

@Entity
public class SchoolClass {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String  name;
    private Integer capacity = 30;//Default capacity to a class is always 30

    @ManyToOne(fetch = FetchType.LAZY)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    private Teacher supervisor;


    public Long getId()                   { return id; }

    public String getName()               { return name; }
    public void   setName(String n)       { this.name = n; }

    public Integer getCapacity()          { return capacity; }
    public void    setCapacity(Integer c) { this.capacity = c; }

    public Grade getGrade()               { return grade; }
    public void  setGrade(Grade g)        { this.grade = g; }

    public Teacher getSupervisor()        { return supervisor; }
    public void    setSupervisor(Teacher t){ this.supervisor = t; }
}
