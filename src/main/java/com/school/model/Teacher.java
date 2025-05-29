package com.school.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Teacher {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phone;
    private String placeOfBirth;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)         
    private User user;


    public User getUser()          { return user; }
    public void setUser(User u)    { this.user = u; }

    @ManyToOne(fetch = FetchType.LAZY)
    private Subject subject;            

    private String img;                 
    private String bloodType;           
    private String sex;                 
    private LocalDate birthday;

    @Column(insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

   
    public Long   getId()            { return id; }
    public String getFullName()      { return fullName; }
    public void   setFullName(String n){ this.fullName = n; }

    public String getEmail()         { return email; }
    public void   setEmail(String e) { this.email = e; }

    public String getPhone()         { return phone; }
    public void   setPhone(String p) { this.phone = p; }

    public String getPlaceOfBirth()         { return placeOfBirth; }
    public void   setPlaceOfBirth(String p) { this.placeOfBirth = p; }

    // public String getPassword()      { return password; }
    // public void   setPassword(String pw){ this.password = pw; }

    public Subject getSubject()      { return subject; }
    public void    setSubject(Subject s){ this.subject = s; }

    public String getImg()           { return img; }
    public void   setImg(String i)   { this.img = i; }

    public String getBloodType()     { return bloodType; }
    public void   setBloodType(String b){ this.bloodType = b; }

    public String getSex()           { return sex; }
    public void   setSex(String s)   { this.sex = s; }

    public LocalDate getBirthday()   { return birthday; }
    public void      setBirthday(LocalDate d){ this.birthday = d; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
}
