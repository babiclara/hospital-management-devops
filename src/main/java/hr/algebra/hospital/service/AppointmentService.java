package hr.algebra.hospital.service;

import hr.algebra.hospital.domain.Appointment;
import hr.algebra.hospital.domain.AppointmentStatus;
import hr.algebra.hospital.repository.AppointmentRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AppointmentService {
    private static final DateTimeFormatter HUMAN =
            DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm");

    private final AppointmentRepository appointments;

    private final Counter booked;
    private final Counter rejected;
    private final Counter cancelled;

    public AppointmentService(AppointmentRepository appointments, MeterRegistry registry) {
        this.appointments = appointments;
        this.booked = Counter.builder("hospital.appointments.booked")
                .description("Appointments successfully scheduled")
                .register(registry);
        this.rejected = Counter.builder("hospital.appointments.rejected")
                .description("Appointments refused because the doctor's slot was taken")
                .register(registry);
        this.cancelled = Counter.builder("hospital.appointments.cancelled")
                .description("Appointments cancelled by staff")
                .register(registry);
    }

    public List<Appointment> findAll() {
        return appointments.findAllByOrderByScheduledAtDesc();
    }

    public List<Appointment> findForDoctor(Long doctorId) {
        return appointments.findByDoctorIdOrderByScheduledAtDesc(doctorId);
    }

    public List<Appointment> findForPatient(Long patientId) {
        return appointments.findByPatientIdOrderByScheduledAtDesc(patientId);
    }

    public Appointment get(Long id) {
        return appointments.findById(id).orElseThrow(() -> new NotFoundException("Appointment", id));
    }

    // Checked here so the receptionist gets a readable message, and again by the
    // unique index, which is what holds when two bookings arrive at once.
    @Transactional
    public Appointment schedule(Appointment appointment) {
        assertSlotFree(appointment);
        try {
            Appointment saved = appointments.saveAndFlush(appointment);
            booked.increment();
            return saved;
        } catch (DataIntegrityViolationException ex) {
            rejected.increment();
            throw new SchedulingConflictException(clashMessage(appointment));
        }
    }

    @Transactional
    public Appointment update(Appointment appointment) {
        assertSlotFree(appointment);
        try {
            return appointments.saveAndFlush(appointment);
        } catch (DataIntegrityViolationException ex) {
            rejected.increment();
            throw new SchedulingConflictException(clashMessage(appointment));
        }
    }

    @Transactional
    public Appointment changeStatus(Long id, AppointmentStatus status) {
        Appointment appointment = get(id);
        appointment.setStatus(status);
        if (status == AppointmentStatus.CANCELLED) {
            cancelled.increment();
        }
        return appointments.save(appointment);
    }

    private void assertSlotFree(Appointment appointment) {
        if (!appointment.occupiesSlot()) {
            return;
        }
        appointments.findClash(appointment.getDoctor().getId(),
                        appointment.getScheduledAt(),
                        appointment.getId())
                .ifPresent(clash -> {
                    rejected.increment();
                    throw new SchedulingConflictException(clashMessage(appointment));
                });
    }

    private String clashMessage(Appointment appointment) {
        return "Dr %s already has an appointment on %s. Choose another time."
                .formatted(appointment.getDoctor().getFullName(),
                           appointment.getScheduledAt().format(HUMAN));
    }

    public long countByStatus(AppointmentStatus status) {
        return appointments.countByStatus(status);
    }
}
