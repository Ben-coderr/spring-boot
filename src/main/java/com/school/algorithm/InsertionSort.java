package com.school.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// simple insertion sort, prioritising clarity over speed
public class InsertionSort<T> implements SortAlgorithm<T> {
    @Override
    public List<T> sort(List<T> data, Comparator<T> comparator) {
        List<T> list = new ArrayList<>(data);
        for (int index = 1; index < list.size(); index++) {
            T key = list.get(index);
            int current = index - 1;
            while (current >= 0 && comparator.compare(list.get(current), key) > 0) {
                list.set(current + 1, list.get(current));
                current--;
            }
            list.set(current + 1, key);
        }
        return list;
    }
}
