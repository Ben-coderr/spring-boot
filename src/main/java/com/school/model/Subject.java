package com.school.model;
import jakarta.persistence.*;

@Entity
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer coefficient = 1;

    // percentage weights for each mark type
    private Double ccWeight;
    private Double examWeight;
    private Double attendanceWeight;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCoefficient() { return coefficient; }
    //set coefficient of subject
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }

    public Double getCcWeight() { return ccWeight; }
    public void setCcWeight(Double ccWeight) { this.ccWeight = ccWeight; }

    public Double getExamWeight() { return examWeight; }
    public void setExamWeight(Double examWeight) { this.examWeight = examWeight; }

    public Double getAttendanceWeight() { return attendanceWeight; }
    public void setAttendanceWeight(Double attendanceWeight) { this.attendanceWeight = attendanceWeight; }
}
