package hr.algebra.hospital.service;

import hr.algebra.hospital.domain.MedicalRecord;
import hr.algebra.hospital.repository.MedicalRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MedicalRecordService {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private final MedicalRecordRepository records;

    public MedicalRecordService(MedicalRecordRepository records) {
        this.records = records;
    }

    public List<MedicalRecord> findForPatient(Long patientId) {
        return records.findByPatientIdOrderByVisitDateDesc(patientId);
    }

    public List<MedicalRecord> findForDoctor(Long doctorId) {
        return records.findByDoctorIdOrderByVisitDateDesc(doctorId);
    }

    public Optional<MedicalRecord> findForAppointment(Long appointmentId) {
        return records.findByAppointmentId(appointmentId);
    }

    public MedicalRecord get(Long id) {
        return records.findById(id).orElseThrow(() -> new NotFoundException("Medical record", id));
    }

    @Transactional
    public MedicalRecord save(MedicalRecord record) {
        return records.save(record);
    }

    public String exportCsv(Long doctorId) {
        StringBuilder csv = new StringBuilder();
        csv.append("visit_date,patient,date_of_birth,diagnosis,notes\n");
        for (MedicalRecord record : findForDoctor(doctorId)) {
            csv.append(quote(record.getVisitDate().format(ISO))).append(',')
               .append(quote(record.getPatient().getFullName())).append(',')
               .append(quote(record.getPatient().getDateOfBirth().format(ISO))).append(',')
               .append(quote(record.getDiagnosis())).append(',')
               .append(quote(record.getNotes()))
               .append('\n');
        }
        return csv.toString();
    }

    // Doubling the quotes stops a comma or newline in a field breaking the columns.
    private static String quote(String value) {
        String safe = value == null ? "" : value.replace("\"", "\"\"");
        return '"' + safe + '"';
    }
}
