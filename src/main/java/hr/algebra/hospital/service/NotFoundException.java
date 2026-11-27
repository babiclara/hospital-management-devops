package hr.algebra.hospital.service;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, Long id) {
        super(entity + " " + id + " was not found");
    }
}
