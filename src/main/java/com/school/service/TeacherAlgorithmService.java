package com.school.service;

import com.school.dto.TeacherDto;
import com.school.dto.TeacherMapper;
import com.school.model.Teacher;
import com.school.repository.TeacherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

// same idea as StudentAlgorithmService but for teachers
@Service
public class TeacherAlgorithmService {

    private final TeacherRepository teacherRepo;
    private final SimpleAlgorithmService algorithms;

    public TeacherAlgorithmService(TeacherRepository teacherRepo,
                                   SimpleAlgorithmService algorithms) {
        this.teacherRepo = teacherRepo;
        this.algorithms = algorithms;
    }

    public List<TeacherDto> sort(String field, String algorithm) {
        List<Teacher> data = teacherRepo.findAll();
        Comparator<Teacher> comparator;
        String fieldLower = field.toLowerCase();
        if (fieldLower.equals("name")) {
            comparator = Comparator.comparing(Teacher::getFullName, String.CASE_INSENSITIVE_ORDER);
        } else if (fieldLower.equals("birthday") || fieldLower.equals("date")) {
            comparator = Comparator.comparing(Teacher::getBirthday);
        } else {
            comparator = Comparator.comparing(Teacher::getId);
        }

        List<Teacher> sorted = algorithm.equalsIgnoreCase("insertion")
                ? algorithms.insertionSort(data, comparator)
                : algorithms.bubbleSort(data, comparator);

        List<TeacherDto> out = new ArrayList<>();
        for (Teacher teacher : sorted) {
            out.add(TeacherDto.from(teacher));
        }
        return out;
    }

    public TeacherDto search(String field, String value) {
        List<Teacher> data = teacherRepo.findAll();
        Predicate<Teacher> predicate;
        String fieldLower = field.toLowerCase();
        if (fieldLower.equals("name")) {
            predicate = teacher -> value.equalsIgnoreCase(teacher.getFullName());
        } else if (fieldLower.equals("id")) {
            try {
                Long parsedId = Long.parseLong(value);
                predicate = teacher -> teacher.getId().equals(parsedId);
            } catch (NumberFormatException ex) {
                predicate = teacher -> false;
            }
        } else {
            predicate = teacher -> false;
        }

        Optional<Teacher> result = algorithms.linearSearch(data, predicate);
        return result.map(TeacherDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher not found"));
    }
}
