package com.school.service;

import com.school.service.SimpleAlgorithmService;
import com.school.dto.StudentDto;
import com.school.dto.StudentMapper;
import com.school.model.Student;
import com.school.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Service that uses simple sorting and searching algorithms. The algorithms
 * can be swapped without impacting callers since only this class depends on
 * their concrete implementations.
 */
@Service
public class StudentAlgorithmService {

    private final StudentRepository studentRepo;
    private final SimpleAlgorithmService algorithms;

    public StudentAlgorithmService(StudentRepository studentRepo,
                                   SimpleAlgorithmService algorithms) {
        this.studentRepo = studentRepo;
        this.algorithms = algorithms;
    }

    public List<StudentDto> sort(String field, String algorithm) {
        List<Student> data = studentRepo.findAll();
        Comparator<Student> comparator;
        switch (field.toLowerCase()) {
            case "name" -> comparator = Comparator.comparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER);
            case "surname" -> comparator = Comparator.comparing(Student::getSurname, String.CASE_INSENSITIVE_ORDER);
            case "birthday", "date" -> comparator = Comparator.comparing(Student::getBirthday);
            default -> comparator = Comparator.comparing(Student::getId);
        }
        List<Student> sorted = "insertion".equalsIgnoreCase(algorithm)
                ? algorithms.insertionSort(data, comparator)
                : algorithms.bubbleSort(data, comparator);
        List<StudentDto> out = new ArrayList<>();
        for (Student s : sorted) {
            out.add(StudentMapper.toDto(s));
        }
        return out;
    }

    public StudentDto search(String field, String value) {
        List<Student> data = studentRepo.findAll();
        Predicate<Student> predicate;
        switch (field.toLowerCase()) {
            case "name" -> predicate = s -> value.equalsIgnoreCase(s.getFullName());
            case "surname" -> predicate = s -> value.equalsIgnoreCase(s.getSurname());
            case "id" -> {
                try {
                    Long id = Long.parseLong(value);
                    predicate = s -> s.getId().equals(id);
                } catch (NumberFormatException ex) {
                    predicate = s -> false;
                }
            }
            default -> predicate = s -> false;
        }
        Optional<Student> result = algorithms.linearSearch(data, predicate);
        return result.map(StudentMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
    }
}
