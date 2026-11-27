package hr.algebra.hospital.repository;

import hr.algebra.hospital.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByActiveTrueOrderByLastNameAsc();

    List<Doctor> findAllByOrderByLastNameAsc();

    List<Doctor> findBySpecializationIgnoreCaseAndActiveTrueOrderByLastNameAsc(String specialization);

    Optional<Doctor> findByUserUsername(String username);
}
