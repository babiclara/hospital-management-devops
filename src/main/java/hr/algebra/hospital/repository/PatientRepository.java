package hr.algebra.hospital.repository;

import hr.algebra.hospital.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    // A blank term matches every patient.
    @Query("""
            SELECT p FROM Patient p
            WHERE (:term IS NULL OR :term = ''
                   OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :term, '%'))
                   OR LOWER(p.lastName)  LIKE LOWER(CONCAT('%', :term, '%')))
              AND (:includeInactive = TRUE OR p.active = TRUE)
            ORDER BY p.lastName, p.firstName
            """)
    Page<Patient> search(@Param("term") String term,
                         @Param("includeInactive") boolean includeInactive,
                         Pageable pageable);
}
