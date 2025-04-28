package com.school.student.model;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.school.common.audit.Auditable;
import com.school.common.enums.Gender;
import com.school.common.enums.StudentStatus;
import com.school.parent.model.Parent;
import com.school.schoolclass.model.SchoolClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Student extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;      // “name” in Prisma
    @Column(name = "surname")       // exact match
    private String surname;    // NEW
    @Column(unique = true, nullable = false, length = 120)
    private String username;
    @Column(unique = true)
  
    private String email;

     
         /* ─────────── NEW FIELDS ─────────── */
     
         /** Date of birth (required for age checks) */
         @Column(nullable = false)
         private LocalDate dateOfBirth;
     
         /** MALE / FEMALE / OTHER */
         @Enumerated(EnumType.STRING)
         @Column(nullable = false, length = 10)
         private Gender gender;
     
         /** Contact phone in E.164 format – optional */
         private String phone;
     
         /** Full postal address – optional, 255 chars max */
         @Column(length = 255)
         private String address;
     
         /** When the student first enrolled */
         private LocalDate enrollmentDate;
     
         /** ACTIVE / GRADUATED / INACTIVE */
         @Enumerated(EnumType.STRING)
         @Column(nullable = false, length = 15)
         private StudentStatus status;
     
         /** Soft-delete flag */
         @Builder.Default
         @Column(nullable = false)
         private boolean deleted = false;    




    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id") 
    private SchoolClass schoolClass;
    @Builder.Default
    @ManyToMany(mappedBy = "children")
    private Set<Parent> parents = new HashSet<>();
}
