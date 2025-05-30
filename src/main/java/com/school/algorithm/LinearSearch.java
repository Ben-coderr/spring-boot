package com.school.algorithm;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Straightforward linear search implementation. The algorithm iterates
 * over every element until a match is found.
 */
public class LinearSearch<T> implements SearchAlgorithm<T> {
    @Override
    public Optional<T> search(List<T> data, Predicate<T> predicate) {
        for (T item : data) {
            if (predicate.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
}
