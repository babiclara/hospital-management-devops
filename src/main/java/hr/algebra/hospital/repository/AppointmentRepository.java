package hr.algebra.hospital.repository;

import hr.algebra.hospital.domain.Appointment;
import hr.algebra.hospital.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByScheduledAtDesc();

    List<Appointment> findByPatientIdOrderByScheduledAtDesc(Long patientId);

    List<Appointment> findByDoctorIdOrderByScheduledAtDesc(Long doctorId);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.scheduledAt = :scheduledAt
              AND a.status <> hr.algebra.hospital.domain.AppointmentStatus.CANCELLED
              AND (:excludedId IS NULL OR a.id <> :excludedId)
            """)
    Optional<Appointment> findClash(@Param("doctorId") Long doctorId,
                                    @Param("scheduledAt") LocalDateTime scheduledAt,
                                    @Param("excludedId") Long excludedId);

    long countByStatus(AppointmentStatus status);
}
