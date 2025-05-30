package com.school.algorithm;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.List;

/**
 * Generic contract for search algorithms. Changing the implementation
 * does not affect the method signature used by callers.
 */
public interface SearchAlgorithm<T> {
    Optional<T> search(List<T> data, Predicate<T> predicate);
}
