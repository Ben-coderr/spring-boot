package com.school.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// simple bubble sort
public class BubbleSort<T> implements SortAlgorithm<T> {
    @Override
    public List<T> sort(List<T> data, Comparator<T> comparator) {
        List<T> list = new ArrayList<>(data);
        int listSize = list.size();
        for (int outer = 0; outer < listSize - 1; outer++) {
            for (int inner = 0; inner < listSize - outer - 1; inner++) {
                if (comparator.compare(list.get(inner), list.get(inner + 1)) > 0) {
                    T temp = list.get(inner);
                    list.set(inner, list.get(inner + 1));
                    list.set(inner + 1, temp);
                }
            }
        }
        return list;
    }
}
