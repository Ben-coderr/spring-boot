package project.edusphere.security;

import project.edusphere.admin.repository.AdminRepository;
import project.edusphere.teacher.repository.TeacherRepository;
import project.edusphere.parent.repository.ParentRepository;
import project.edusphere.student.repository.StudentRepository;
import project.edusphere.common.enums.Role;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DbUserDetailsService implements UserDetailsService {

    private final AdminRepository   admins;
    private final TeacherRepository teachers;
    private final ParentRepository  parents;
    private final StudentRepository students;
    private final PasswordEncoder   encoder;   // keep for seeding

    @Override
    public UserDetails loadUserByUsername(String email) {
        return admins.findByEmail(email).map(this::map)
            .orElseGet(() -> teachers.findByEmail(email).map(this::map)
            .orElseGet(() -> parents.findByEmail(email).map(this::map)
            .orElseGet(() -> students.findByEmail(email).map(this::map)
            .orElseThrow(() -> new UsernameNotFoundException(email)))));
    }


    private UserDetails map(Object u) {
        String pw; Role role;
        if (u instanceof project.edusphere.admin.model.Admin a)         { pw = a.getPassword(); role = a.getRole(); }
        else if (u instanceof project.edusphere.teacher.model.Teacher t){ pw = t.getPassword(); role = t.getRole(); }
        else if (u instanceof project.edusphere.parent.model.Parent p)  { pw = p.getPassword(); role = p.getRole(); }
        else                                                     { pw = ((project.edusphere.student.model.Student)u).getPassword();
                                                                    role = ((project.edusphere.student.model.Student)u).getRole(); }

        return User.withUsername(getEmail(u))
                   .password(pw)
                   .authorities(role.asAuthority())
                   .build();
    }

 private String getEmail(Object o) {
     if (o instanceof project.edusphere.admin.model.Admin a) {
         return a.getEmail();
     } else if (o instanceof project.edusphere.teacher.model.Teacher t) {
         return t.getEmail();
     } else if (o instanceof project.edusphere.parent.model.Parent p) {
         return p.getEmail();
     } else { // Student
         return ((project.edusphere.student.model.Student) o).getEmail();
     }
 }
}
