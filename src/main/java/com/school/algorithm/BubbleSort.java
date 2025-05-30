package com.school.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Very simple bubble sort implementation. This class focuses on clarity
 * rather than performance and can easily be replaced by a different
 * algorithm without changing its public contract.
 */
public class BubbleSort<T> implements SortAlgorithm<T> {
    @Override
    public List<T> sort(List<T> data, Comparator<T> comparator) {
        List<T> list = new ArrayList<>(data);
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (comparator.compare(list.get(j), list.get(j + 1)) > 0) {
                    T tmp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, tmp);
                }
            }
        }
        return list;
    }
}
