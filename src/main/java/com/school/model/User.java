package com.school.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean approved = false;

    public boolean isApproved()           { return approved; }
    public void    setApproved(boolean a) { this.approved = a; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public String getPassword()     { return password; }
    @Override public String getUsername()     { return username; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    //getters setters
    public Long getId()               { return id; }
    public Role getRole()             { return role; }
    public void setRole(Role role)    { this.role = role; }
    public void setUsername(String u) { this.username = u; }
    public void setPassword(String p) { this.password = p; }
}
