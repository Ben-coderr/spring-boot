package com.school.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Basic insertion sort algorithm for demonstration. This
 * implementation emphasises simplicity over efficiency.
 */
public class InsertionSort<T> implements SortAlgorithm<T> {
    @Override
    public List<T> sort(List<T> data, Comparator<T> comparator) {
        List<T> list = new ArrayList<>(data);
        for (int i = 1; i < list.size(); i++) {
            T key = list.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
        return list;
    }
}
