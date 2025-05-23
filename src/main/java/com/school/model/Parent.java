package com.school.model;

import jakarta.persistence.*;

@Entity
public class Parent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String password;

    @Column(insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;   // for auditing and it is filled automaticlly by the db


    public Long getId()                { return id; }
    public String getFullName()        { return fullName; }
    public void   setFullName(String n){ this.fullName = n; }

    public String getPhone()           { return phone; }
    public void   setPhone(String p)   { this.phone = p; }

    public String getEmail()           { return email; }
    public void   setEmail(String e)   { this.email = e; }

    public String getAddress()         { return address; }
    public void   setAddress(String a) { this.address = a; }

    public String getPassword()         { return password; }
    public void   setPassword(String pass) { this.password = pass; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
}
