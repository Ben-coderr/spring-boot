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

// service using simple sort and search algorithms

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
        String fieldLower = field.toLowerCase();
        if (fieldLower.equals("name")) {
            comparator = Comparator.comparing(Student::getFullName, String.CASE_INSENSITIVE_ORDER);
        } else if (fieldLower.equals("surname")) {
            comparator = Comparator.comparing(Student::getSurname, String.CASE_INSENSITIVE_ORDER);
        } else if (fieldLower.equals("birthday") || fieldLower.equals("date")) {
            comparator = Comparator.comparing(Student::getBirthday);
        } else {
            comparator = Comparator.comparing(Student::getId);
        }

        List<Student> sorted = algorithm.equalsIgnoreCase("insertion")
                ? algorithms.insertionSort(data, comparator)
                : algorithms.bubbleSort(data, comparator);

        List<StudentDto> out = new ArrayList<>();
        for (Student student : sorted) {
            out.add(StudentMapper.toDto(student));
        }
        return out;
    }

    public StudentDto search(String field, String value) {
        List<Student> data = studentRepo.findAll();
        Predicate<Student> predicate;
        String fieldLower = field.toLowerCase();
        if (fieldLower.equals("name")) {
            predicate = student -> value.equalsIgnoreCase(student.getFullName());
        } else if (fieldLower.equals("surname")) {
            predicate = student -> value.equalsIgnoreCase(student.getSurname());
        } else if (fieldLower.equals("id")) {
            try {
                Long parsedId = Long.parseLong(value);
                predicate = student -> student.getId().equals(parsedId);
            } catch (NumberFormatException ex) {
                predicate = student -> false;
            }
        } else {
            predicate = student -> false;
        }

        Optional<Student> result = algorithms.linearSearch(data, predicate);
        return result.map(StudentMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "student not found"));
    }
}
