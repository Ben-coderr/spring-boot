package com.school.model;
import jakarta.persistence.*;

// Result of an exam for a particular student

@Entity
public class Result {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double score;

    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    private Exam exam;

    @ManyToOne(optional = false)          
    @JoinColumn(name = "subject_id")      
    private Subject subject;
    @Column(name = "cc_score")
    private Double ccScore;   // continuous assessment
    @Column(name = "exam_score")
    private Double examScore; // exam mark
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
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public Double getCcScore() { return ccScore; }
    public void setCcScore(Double ccScore) { this.ccScore = ccScore; }
    public Double getExamScore() { return examScore; }
    public void setExamScore(Double examScore) { this.examScore = examScore; }
    public Boolean getIsFinal() { return isFinal; }
    public void setIsFinal(Boolean isFinal) { this.isFinal = isFinal; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
}

