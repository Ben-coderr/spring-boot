package com.school.model;

import jakarta.persistence.*;
import java.time.LocalDate;


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
    public void setUser(User user) { this.user = user; }

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
    //set teacher full name
    public void   setFullName(String name){ this.fullName = name; }

    public String getEmail()         { return email; }
    //set teacher email
    public void   setEmail(String email) { this.email = email; }

    public String getPhone()         { return phone; }
    //set teacher phone
    public void   setPhone(String phone) { this.phone = phone; }

    public String getPlaceOfBirth()         { return placeOfBirth; }
    //set place of birth
    public void   setPlaceOfBirth(String place) { this.placeOfBirth = place; }

    // public String getPassword()      { return password; }
    // public void   setPassword(String pw){ this.password = pw; }

    public Subject getSubject()      { return subject; }
    //set subject taught
    public void    setSubject(Subject subject){ this.subject = subject; }

    public String getImg()           { return img; }
    //set teacher image
    public void   setImg(String img)   { this.img = img; }

    public String getBloodType()     { return bloodType; }
    //set blood type
    public void   setBloodType(String blood){ this.bloodType = blood; }

    public String getSex()           { return sex; }
    //set teacher sex
    public void   setSex(String sex)   { this.sex = sex; }

    public LocalDate getBirthday()   { return birthday; }
    //set teacher birthday
    public void      setBirthday(LocalDate date){ this.birthday = date; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
}
