package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1. Retrieve appointments for a doctor in time range
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
            Long doctorId, LocalDateTime start, LocalDateTime end
    );

    // 2. Retrieve appointments by doctor + patient name (case-insensitive) in time range
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor d " +
           "LEFT JOIN FETCH a.patient p " +
           "WHERE d.id = :doctorId " +
           "AND LOWER(p.name) LIKE CONCAT('%', LOWER(:patientName), '%') " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId, String patientName, LocalDateTime start, LocalDateTime end
    );

    // 3. Delete all appointments for a given doctor
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    // 4. All appointments for a patient
    List<Appointment> findByPatientId(Long patientId);

    // 5. Appointments for a patient by status, ascending appointment time
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    // 6. Filter by doctor name (case-insensitive) + patientId
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE CONCAT('%', LOWER(:doctorName), '%') " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    // 7. Filter by doctor name (case-insensitive) + patientId + status
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE CONCAT('%', LOWER(:doctorName), '%') " +
           "AND a.patient.id = :patientId " +
           "AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);

    // Optional status update method
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(int status, long id);
}
