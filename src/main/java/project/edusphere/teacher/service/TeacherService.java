package project.edusphere.teacher.service;

import project.edusphere.teacher.dto.TeacherDTO;
import project.edusphere.teacher.model.Teacher;
import project.edusphere.teacher.repository.TeacherRepository;
import project.edusphere.common.enums.Role;
import project.edusphere.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository repo;
    private final PasswordEncoder encoder;

    @Transactional(readOnly = true)
    public List<TeacherDTO> findAll() {
        List<Teacher> entities = repo.findAll();
        List<TeacherDTO> result = new ArrayList<>();

        for (Teacher t : entities) {
            result.add(toDto(t));
        }
        return result;
    }

    public TeacherDTO findById(Long id) {
        Optional<Teacher> opt = repo.findById(id);

        if (opt.isEmpty()) {
            throw new NotFoundException("Teacher " + id + " not found");
        }

        return toDto(opt.get());
    }

    public TeacherDTO create(TeacherDTO dto) {

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("password is required");
        }

        // build entity “old-school” style
        Teacher teacher = new Teacher();
        teacher.setFullName(dto.getFullName());
        teacher.setEmail(dto.getEmail());
        teacher.setPassword(encoder.encode(dto.getPassword()));
        teacher.setRole(Role.TEACHER);

        Teacher saved = repo.save(teacher);
        return toDto(saved);
    }
    public TeacherDTO update(Long id, TeacherDTO dto) {

        Optional<Teacher> opt = repo.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Teacher " + id + " not found");
        }

        Teacher t = opt.get();
        t.setFullName(dto.getFullName());
        t.setEmail(dto.getEmail());

        Teacher saved = repo.save(t);
        return toDto(saved);
    }
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Teacher " + id + " not found");
        }
        repo.deleteById(id);
    }
    public List<TeacherDTO> findBySubject(Long subjectId) {

        List<Teacher> teachers = repo.findAllBySubjects_Id(subjectId);

        if (teachers.isEmpty()) {
            throw new NotFoundException("No teachers found for subject " + subjectId);
        }

        List<TeacherDTO> dtoList = new ArrayList<>();
        for (Teacher t : teachers) {
            dtoList.add(toDto(t));
        }
        return dtoList;
    }
    private TeacherDTO toDto(Teacher t) {
        TeacherDTO dto = new TeacherDTO();
        dto.setId(t.getId());
        dto.setFullName(t.getFullName());
        dto.setEmail(t.getEmail());
 
        return dto;
    }
}
