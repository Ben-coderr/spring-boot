package com.school.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String surname;

    private String email;
    private String phone;
    private String password;

    private String address;
    private String img;
    private String bloodType;
    private String sex;
    private LocalDate birthday;

    @Column(insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    //Rleations variables for parent link top student
    @ManyToOne(fetch = FetchType.LAZY)
    private Parent parent;                // many kids can possibly share one parent

    @ManyToOne(fetch = FetchType.LAZY)
    private SchoolClass schoolClass;


    public Long   getId()                 { return id; }

    public String getFullName()           { return fullName; }
    public void   setFullName(String n)   { this.fullName = n; }

    public String getSurname()            { return surname; }
    public void   setSurname(String s)    { this.surname = s; }

    public String getEmail()              { return email; }
    public void   setEmail(String e)      { this.email = e; }

    public String getPhone()              { return phone; }
    public void   setPhone(String p)      { this.phone = p; }

    public String getPassword()           { return password; }
    public void   setPassword(String pw)  { this.password = pw; }

    public String getAddress()            { return address; }
    public void   setAddress(String a)    { this.address = a; }

    public String getImg()                { return img; }
    public void   setImg(String i)        { this.img = i; }

    public String getBloodType()          { return bloodType; }
    public void   setBloodType(String b)  { this.bloodType = b; }

    public String getSex()                { return sex; }
    public void   setSex(String s)        { this.sex = s; }

    public LocalDate getBirthday()        { return birthday; }
    public void      setBirthday(LocalDate d){ this.birthday = d; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }

    public Parent getParent()             { return parent; }
    public void   setParent(Parent p)     { this.parent = p; }

    public SchoolClass getSchoolClass()   { return schoolClass; }
    public void        setSchoolClass(SchoolClass c){ this.schoolClass = c; }
}
