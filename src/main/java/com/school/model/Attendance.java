package com.school.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Attendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;        // PRESENT / ABSENT / LATE
    private LocalDate date;       

    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lesson lesson;


    public Long getId()                  { return id; }
    public String getStatus()            { return status; }
    public void   setStatus(String s)    { this.status = s; }

    public LocalDate getDate()           { return date; }
    public void      setDate(LocalDate d){ this.date = d; }

    public Student getStudent()          { return student; }
    public void   setStudent(Student st) { this.student = st; }

    public Lesson  getLesson()           { return lesson; }
    public void    setLesson(Lesson l)   { this.lesson = l; }
}
