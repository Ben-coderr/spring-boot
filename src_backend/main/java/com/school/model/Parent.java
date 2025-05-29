package com.school.model;

// import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
public class Parent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String email;
    private String address;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)      
    private User user;


    public User getUser()          { return user; }
    public void setUser(User user) { this.user = user; }

    @Column(insertable = false, updatable = false)
    private java.time.LocalDateTime createdAt;   // for auditing and it is filled automaticlly by the db


    public Long getId()                { return id; }
    public String getFullName()        { return fullName; }
    //set parent full name
    public void   setFullName(String name){ this.fullName = name; }

    public String getPhone()           { return phone; }
    //set parent phone
    public void   setPhone(String phone)   { this.phone = phone; }

    public String getEmail()           { return email; }
    //set parent email
    public void   setEmail(String email)   { this.email = email; }

    public String getAddress()         { return address; }
    //set parent address
    public void   setAddress(String address) { this.address = address; }

    // public String getPassword()         { return password; }
    // public void   setPassword(String pass) { this.password = pass; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
}
