package hr.algebra.hospital.service;

import hr.algebra.hospital.domain.Patient;
import hr.algebra.hospital.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PatientService {
    private final PatientRepository patients;

    public PatientService(PatientRepository patients) {
        this.patients = patients;
    }

    public Page<Patient> search(String term, boolean includeInactive, Pageable pageable) {
        return patients.search(term == null ? "" : term.trim(), includeInactive, pageable);
    }

    public Patient get(Long id) {
        return patients.findById(id).orElseThrow(() -> new NotFoundException("Patient", id));
    }

    @Transactional
    public Patient save(Patient patient) {
        return patients.save(patient);
    }

    @Transactional
    public Patient setActive(Long id, boolean active) {
        Patient patient = get(id);
        patient.setActive(active);
        return patients.save(patient);
    }

    public long count() {
        return patients.count();
    }
}
