package hr.algebra.hospital.repository;

import hr.algebra.hospital.domain.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(Long patientId);

    List<MedicalRecord> findByDoctorIdOrderByVisitDateDesc(Long doctorId);

    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);
}
