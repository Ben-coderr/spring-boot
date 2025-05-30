package com.school.algorithm;

import java.util.Comparator;
import java.util.List;

// Generic interface  for sorting algorithms for implementing anywhere
public interface SortAlgorithm<T> {
    List<T> sort(List<T> data, Comparator<T> comparator);
}
