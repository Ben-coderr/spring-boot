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
    //get attendance status
    public String getStatus()            { return status; }
    //set attendance status
    public void   setStatus(String status)    { this.status = status; }

    //get attendance date
    public LocalDate getDate()           { return date; }
    //set attendance date
    public void      setDate(LocalDate date){ this.date = date; }

    //get student for record
    public Student getStudent()          { return student; }
    //set student for record
    public void   setStudent(Student student) { this.student = student; }

    //get lesson for record
    public Lesson  getLesson()           { return lesson; }
    //set lesson for record
    public void    setLesson(Lesson lesson)   { this.lesson = lesson; }
}
