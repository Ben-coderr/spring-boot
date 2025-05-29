package com.school.model;
import jakarta.persistence.*;

/**
 * Result of an exam for a particular student. Can optionally be linked to a
 * {@link com.school.model.MarkComponent} when marks are broken down into
 * components such as "CC" or "EXAM". Keeping the entity simple allows the
 * repository queries to remain straightforward.
 */

@Entity
public class Result {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double score;
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    private Exam exam;
    @ManyToOne(fetch = FetchType.LAZY)
    private MarkComponent component;
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
    public MarkComponent getComponent() { return component; }
    public void setComponent(MarkComponent component) { this.component = component; }
    public Boolean getIsFinal() { return isFinal; }
    public void setIsFinal(Boolean isFinal) { this.isFinal = isFinal; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
}

