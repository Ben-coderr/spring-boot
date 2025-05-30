package com.school.service;

import com.school.algorithm.BubbleSort;
import com.school.algorithm.InsertionSort;
import com.school.algorithm.LinearSearch;
import com.school.algorithm.SearchAlgorithm;
import com.school.algorithm.SortAlgorithm;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Generic service providing simple sorting and searching facilities. It
 * is deliberately basic so that other parts of the application can use the
 * algorithms without knowing their implementations.
 */
import org.springframework.stereotype.Service;

@Service
public class SimpleAlgorithmService {

    /** Sort the provided data using bubble sort. */
    public <T> List<T> bubbleSort(List<T> data, Comparator<T> comparator) {
        SortAlgorithm<T> algo = new BubbleSort<>();
        return algo.sort(data, comparator);
    }

    /** Sort the provided data using insertion sort. */
    public <T> List<T> insertionSort(List<T> data, Comparator<T> comparator) {
        SortAlgorithm<T> algo = new InsertionSort<>();
        return algo.sort(data, comparator);
    }

    /**
     * Perform a linear search over the data. Returns an empty Optional
     * if no element satisfies the predicate.
     */
    public <T> Optional<T> linearSearch(List<T> data, Predicate<T> predicate) {
        SearchAlgorithm<T> algo = new LinearSearch<>();
        return algo.search(data, predicate);
    }
}
