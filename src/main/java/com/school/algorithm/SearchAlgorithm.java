package com.school.algorithm;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.List;

// Generic interface for search algorithms. 

public interface SearchAlgorithm<T> {
    Optional<T> search(List<T> data, Predicate<T> predicate);
}
