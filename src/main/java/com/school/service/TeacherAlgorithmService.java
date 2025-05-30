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

/**
 * Same as {@link StudentAlgorithmService} but for teachers, demonstrating that
 * the algorithms can easily be reused for other entities.
 */
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
        switch (field.toLowerCase()) {
            case "name" -> comparator = Comparator.comparing(Teacher::getFullName, String.CASE_INSENSITIVE_ORDER);
            case "birthday", "date" -> comparator = Comparator.comparing(Teacher::getBirthday);
            default -> comparator = Comparator.comparing(Teacher::getId);
        }
        List<Teacher> sorted = "insertion".equalsIgnoreCase(algorithm)
                ? algorithms.insertionSort(data, comparator)
                : algorithms.bubbleSort(data, comparator);
        List<TeacherDto> out = new ArrayList<>();
        for (Teacher t : sorted) {
            out.add(TeacherDto.from(t));
        }
        return out;
    }

    public TeacherDto search(String field, String value) {
        List<Teacher> data = teacherRepo.findAll();
        Predicate<Teacher> predicate;
        switch (field.toLowerCase()) {
            case "name" -> predicate = t -> value.equalsIgnoreCase(t.getFullName());
            case "id" -> {
                try {
                    Long id = Long.parseLong(value);
                    predicate = t -> t.getId().equals(id);
                } catch (NumberFormatException ex) {
                    predicate = t -> false;
                }
            }
            default -> predicate = t -> false;
        }
        Optional<Teacher> result = algorithms.linearSearch(data, predicate);
        return result.map(TeacherDto::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "teacher not found"));
    }
}
