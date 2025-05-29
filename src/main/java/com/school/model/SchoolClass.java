package com.school.model;

import jakarta.persistence.*;

@Entity
public class SchoolClass { // class group

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String  name;
    private Integer capacity = 30;//Default capacity to a class is always 30

    @ManyToOne(fetch = FetchType.LAZY)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    private Teacher supervisor;


    public Long getId()                   { return id; }

    //get class name
    public String getName()               { return name; }
    //set class name
    public void   setName(String name)       { this.name = name; }

    //get class capacity
    public Integer getCapacity()          { return capacity; }
    //set class capacity
    public void    setCapacity(Integer capacity) { this.capacity = capacity; }

    //get grade for the class
    public Grade getGrade()               { return grade; }
    //set grade for the class
    public void  setGrade(Grade grade)        { this.grade = grade; }

    //get supervisor teacher
    public Teacher getSupervisor()        { return supervisor; }
    //set supervisor teacher
    public void    setSupervisor(Teacher supervisor){ this.supervisor = supervisor; }
}
