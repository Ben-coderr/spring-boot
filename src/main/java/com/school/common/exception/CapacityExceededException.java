package com.school.common.exception;

public class CapacityExceededException extends RuntimeException {
    public CapacityExceededException(long classId, int capacity) {
        super("Class " + classId + " is already full (capacity=" + capacity + ")");
    }
}
