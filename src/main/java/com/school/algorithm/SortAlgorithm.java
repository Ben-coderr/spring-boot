package com.school.algorithm;

import java.util.Comparator;
import java.util.List;

/**
 * Generic contract for sorting algorithms.
 * Implementations should not change method signature so they can be swapped
 * without affecting callers.
 */
public interface SortAlgorithm<T> {
    List<T> sort(List<T> data, Comparator<T> comparator);
}
