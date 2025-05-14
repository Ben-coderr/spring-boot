package project.edusphere.student.service;
import project.edusphere.common.enums.Role;
import project.edusphere.common.enums.StudentStatus;
import project.edusphere.student.dto.StudentDTO;
import project.edusphere.student.model.Student;
import project.edusphere.student.repository.StudentRepository;
import project.edusphere.common.exception.NotFoundException;
import project.edusphere.parent.repository.ParentRepository;
import project.edusphere.schoolclass.model.SchoolClass;
import project.edusphere.schoolclass.repository.SchoolClassRepository;
import project.edusphere.grade.repository.GradeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

import project.edusphere.common.exception.CapacityExceededException;
import project.edusphere.parent.model.Parent;





@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository repo;
     private final SchoolClassRepository classRepo; 
     private final ParentRepository parentRepo;
    private final GradeRepository      gradeRepo;
    private final PasswordEncoder encoder;

    @Transactional(readOnly = true)
    public List<StudentDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public StudentDTO findById(Long id) {
        return toDto(repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Student " + id + " not found")));
    }

    public StudentDTO create(StudentDTO dto) {
        SchoolClass sc = classRepo.findById(dto.getClassId())
                              .orElseThrow(() -> new NotFoundException("Class "+dto.getClassId()+" not found"));
        if (sc.getStudents().size() >= sc.getCapacity()) {
            throw new CapacityExceededException(sc.getId(), sc.getCapacity());
        }


        long headCount = repo.countBySchoolClass_Id(sc.getId());
        if (headCount >= sc.getCapacity()) {
            throw new CapacityExceededException(sc.getId(), sc.getCapacity());
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new IllegalArgumentException("password is required");    
        dto.setRole(Role.STUDENT);

        if (dto.getDateOfBirth() == null)
            throw new IllegalArgumentException("dateOfBirth is required");
        if (dto.getGender() == null)
            throw new IllegalArgumentException("gender is required");

        Student entity = toEntity(dto);
        Student secured = Student.withRawPassword(entity, dto.getPassword(), encoder);
        return toDto(repo.save(secured));
    }

    public StudentDTO update(Long id, StudentDTO dto) {
        Student st = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Student " + id + " not found"));
        st.setFullName(dto.getFullName());
        st.setSurname(dto.getSurname());
        st.setUsername(dto.getUsername());
        st.setEmail(dto.getEmail());
        st.setDateOfBirth(dto.getDateOfBirth());
        st.setGender(dto.getGender());
        st.setPhone(dto.getPhone());
        st.setAddress(dto.getAddress());
        st.setEnrollmentDate(dto.getEnrollmentDate());
        st.setStatus(dto.getStatus());
        st.setStatus(dto.getStatus());
        st.setImg(dto.getImg());
        st.setBloodType(dto.getBloodType());
        return toDto(repo.save(st));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Student " + id + " not found");
        repo.deleteById(id);
    }

    /* ---------- mapping helpers ---------- */
    public StudentDTO toDto(Student s) {
        return StudentDTO.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .surname(s.getSurname())
                .username(s.getUsername())
                .email(s.getEmail())
                .classId(s.getSchoolClass().getId())        // NEW
                .dateOfBirth(s.getDateOfBirth())
                .gender(s.getGender())
                .phone(s.getPhone())
                .address(s.getAddress())
                .enrollmentDate(s.getEnrollmentDate())
                .gradeId(s.getGrade() == null ? null : s.getGrade().getId())
                .status(s.getStatus())
                .img(s.getImg())
                .bloodType(s.getBloodType())
                .parentIds(
                    s.getParents().stream()
                    .map(Parent::getId)
                    .collect(Collectors.toSet())
                )

                .build();
    }

    private Student toEntity(StudentDTO d) {
        return Student.builder()
                .fullName(d.getFullName())
                .surname(d.getSurname())
                .username(d.getUsername())
                .email(d.getEmail())
                .dateOfBirth(d.getDateOfBirth())
                .gender(d.getGender())
                .phone(d.getPhone())
                .address(d.getAddress())
                .enrollmentDate(d.getEnrollmentDate())
                .grade(
                    d.getGradeId() == null ? null
                                        : gradeRepo.findById(d.getGradeId())
                                                .orElseThrow(() ->
                                                    new NotFoundException("Grade "+d.getGradeId()+" not found"))
                )
                .status(
                    d.getStatus() != null ? d.getStatus()
                                        : StudentStatus.ACTIVE)
                .parents(resolveParents(d.getParentIds()))
                .schoolClass(
                        classRepo.findById(d.getClassId())
                          .orElseThrow(() -> new NotFoundException("Class "+d.getClassId()+" not found"))
                )
                .img(d.getImg())
                .bloodType(d.getBloodType())
                .build();

    }
//Helpers 

private Set<Parent> resolveParents(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) return new HashSet<>();
    return ids.stream()
              .map(id -> parentRepo.findById(id)
                     .orElseThrow(() -> new NotFoundException("Parent "+id+" not found")))
              .collect(Collectors.toSet());
}
}
