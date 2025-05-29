package com.school.model;

import jakarta.persistence.*;
import java.time.LocalDate;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;

@Entity
public class Student { // student entity

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message="name is required")
    private String fullName;
    private String surname;

    @NotBlank(message="email is required")
    @Email(message="invalid email")
    private String email;
    private String phone;

    private String matricule;
    private String placeOfBirth;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)       
    private User user;


    public User getUser()          { return user; }
    public void setUser(User user) { this.user = user; }

    private String address;
    private String img;
    private String bloodType;
    private String sex;
    private LocalDate birthday;

    @Column(insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    // link parent to student
    @ManyToOne(fetch = FetchType.LAZY)
    private Parent parent;                // many kids can possibly share one parent

    @ManyToOne(fetch = FetchType.LAZY)
    private SchoolClass schoolClass;


    public Long   getId()                 { return id; }

    public String getFullName()           { return fullName; }
    //set student full name
    public void   setFullName(String name)   { this.fullName = name; }

    public String getSurname()            { return surname; }
    //set student surname
    public void   setSurname(String surname)    { this.surname = surname; }

    public String getEmail()              { return email; }
    //set student email
    public void   setEmail(String email)      { this.email = email; }

    public String getPhone()              { return phone; }
    //set student phone
    public void   setPhone(String phone)      { this.phone = phone; }

    public String getMatricule()          { return matricule; }
    //set student matricule
    public void   setMatricule(String matricule)  { this.matricule = matricule; }

    public String getPlaceOfBirth()          { return placeOfBirth; }
    //set place of birth
    public void   setPlaceOfBirth(String place)  { this.placeOfBirth = place; }

    // public String getPassword()           { return password; }
    // public void   setPassword(String pw)  { this.password = pw; }

    public String getAddress()            { return address; }
    //set student address
    public void   setAddress(String address)    { this.address = address; }

    public String getImg()                { return img; }
    //set student image
    public void   setImg(String img)        { this.img = img; }

    public String getBloodType()          { return bloodType; }
    //set blood type
    public void   setBloodType(String blood)  { this.bloodType = blood; }

    public String getSex()                { return sex; }
    //set student sex
    public void   setSex(String sex)        { this.sex = sex; }

    public LocalDate getBirthday()        { return birthday; }
    //set student birthday
    public void      setBirthday(LocalDate date){ this.birthday = date; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }

    public Parent getParent()             { return parent; }
    //set parent of student
    public void   setParent(Parent parent)     { this.parent = parent; }

    public SchoolClass getSchoolClass()   { return schoolClass; }
    //set class of student
    public void        setSchoolClass(SchoolClass schoolClass){ this.schoolClass = schoolClass; }
}
