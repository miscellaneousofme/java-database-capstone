package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService; // TokenService validates tokens & extracts IDs

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
    }

    /**
     * Book a new appointment.
     * @return 1 if booked successfully, 0 if any error.
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Update an existing appointment with validation.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment existing = existingOpt.get();

        // Optional: validate if doctor exists
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOpt.isEmpty()) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Example custom validation hook call
        // if (!validateAppointment(appointment)) {
        //     response.put("message", "Invalid appointment details");
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        // }

        // Update allowed fields
        existing.setDoctor(appointment.getDoctor());
        existing.setPatient(appointment.getPatient());
        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());

        appointmentRepository.save(existing);

        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an existing appointment.
     * Only the patient who booked it can cancel.
     */
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> apptOpt = appointmentRepository.findById(id);
        if (apptOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment appt = apptOpt.get();

        // Extract patientId from token
        Long patientIdFromToken = tokenService.getPatientIdFromToken(token);
        if (!appt.getPatient().getId().equals(patientIdFromToken)) {
            response.put("message", "Unauthorized to cancel this appointment");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        appointmentRepository.delete(appt);
        response.put("message", "Appointment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get appointments for a doctor on a specific date, optionally filtered by patient name.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();

        // Extract doctorId from token
        Long doctorId = tokenService.getDoctorIdFromToken(token);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> appointments;
        if (pname != null && !pname.trim().isEmpty() && !"null".equalsIgnoreCase(pname)) {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, pname.trim(), start, end);
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(
                            doctorId, start, end);
        }

        result.put("appointments", appointments);
        return result;
    }

    /**
     * Change status of an appointment (e.g., mark as completed).
     */
    @Transactional
    public ResponseEntity<Map<String, String>> changeStatus(long id, int status) {
        Map<String, String> response = new HashMap<>();

        Optional<Appointment> apptOpt = appointmentRepository.findById(id);
        if (apptOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Appointment appt = apptOpt.get();
        appt.setStatus(status);
        appointmentRepository.save(appt);

        response.put("message", "Status updated successfully");
        return ResponseEntity.ok(response);
    }

// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.


}
