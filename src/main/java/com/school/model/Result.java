package com.school.model;
import jakarta.persistence.*;

@Entity
public class Result {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double score;
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    private Exam exam;
    private Boolean isFinal;

    // 'CC', 'EXAM' or 'ATTENDANCE'
    private String kind;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }
    public Boolean getIsFinal() { return isFinal; }
    public void setIsFinal(Boolean isFinal) { this.isFinal = isFinal; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
}

