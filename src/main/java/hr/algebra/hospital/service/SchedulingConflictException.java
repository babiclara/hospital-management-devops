package hr.algebra.hospital.service;

public class SchedulingConflictException extends RuntimeException {
    public SchedulingConflictException(String message) {
        super(message);
    }
}
