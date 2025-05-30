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

// basic service for sort and search algorithms

import org.springframework.stereotype.Service;

@Service
public class SimpleAlgorithmService {

    // sort using bubble sort
    public <T> List<T> bubbleSort(List<T> data, Comparator<T> comparator) {
        SortAlgorithm<T> algo = new BubbleSort<>();
        return algo.sort(data, comparator);
    }

    // sort using insertion sort
    public <T> List<T> insertionSort(List<T> data, Comparator<T> comparator) {
        SortAlgorithm<T> algo = new InsertionSort<>();
        return algo.sort(data, comparator);
    }

    // linear search, returns empty Optional when no match
    public <T> Optional<T> linearSearch(List<T> data, Predicate<T> predicate) {
        SearchAlgorithm<T> algo = new LinearSearch<>();
        return algo.search(data, predicate);
    }
}
