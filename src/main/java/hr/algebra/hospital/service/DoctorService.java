package hr.algebra.hospital.service;

import hr.algebra.hospital.domain.Doctor;
import hr.algebra.hospital.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DoctorService {
    private final DoctorRepository doctors;

    public DoctorService(DoctorRepository doctors) {
        this.doctors = doctors;
    }

    public List<Doctor> findAll() {
        return doctors.findAllByOrderByLastNameAsc();
    }

    public List<Doctor> findAvailable() {
        return doctors.findByActiveTrueOrderByLastNameAsc();
    }

    public List<Doctor> findBySpecialization(String specialization) {
        return doctors.findBySpecializationIgnoreCaseAndActiveTrueOrderByLastNameAsc(specialization);
    }

    public Doctor get(Long id) {
        return doctors.findById(id).orElseThrow(() -> new NotFoundException("Doctor", id));
    }

    public Optional<Doctor> findByUsername(String username) {
        return doctors.findByUserUsername(username);
    }

    @Transactional
    public Doctor save(Doctor doctor) {
        return doctors.save(doctor);
    }

    @Transactional
    public Doctor setActive(Long id, boolean active) {
        Doctor doctor = get(id);
        doctor.setActive(active);
        return doctors.save(doctor);
    }
}
