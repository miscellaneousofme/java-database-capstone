package com.project.back_end.services;

import com.project.back_end.models.Patient;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 1. Create a new patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Failure
        }
    }

    // 2. Get appointments for a patient (authorized by token)
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null || !patient.getId().equals(id)) {
                resp.put("message", "Unauthorized or patient not found.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
            }
            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> dtos = appointments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            resp.put("appointments", dtos);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    // 3. Filter appointments by condition ("past" or "future")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> resp = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                resp.put("message", "Invalid condition. Use 'past' or 'future'.");
                return ResponseEntity.badRequest().body(resp);
            }
            List<Appointment> filtered = appointmentRepository
                .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);
            List<AppointmentDTO> dtos = filtered.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            resp.put("appointments", dtos);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    // 4. Filter appointments by doctor name (partial match)
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            List<Appointment> filtered = appointmentRepository
                .filterByDoctorNameAndPatientId(name, patientId);
            List<AppointmentDTO> dtos = filtered.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            resp.put("appointments", dtos);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    // 5. Filter appointments by doctor and condition
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> resp = new HashMap<>();
        try {
            int status;
            if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else {
                resp.put("message", "Invalid condition. Use 'past' or 'future'.");
                return ResponseEntity.badRequest().body(resp);
            }
            List<Appointment> filtered = appointmentRepository
                .filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);
            List<AppointmentDTO> dtos = filtered.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
            resp.put("appointments", dtos);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    // 6. Fetch patient details by JWT token
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> resp = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                resp.put("message", "Patient not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
            }
            resp.put("patient", patient); // Optionally map to DTO if you prefer
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            resp.put("message", "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
        }
    }

    /** Helper to convert Appointment to DTO **/
    private AppointmentDTO toDTO(Appointment appt) {
        Doctor d = appt.getDoctor();
        Patient p = appt.getPatient();
        return new AppointmentDTO(
            appt.getId(),
            d != null ? d.getId() : null,
            d != null ? d.getName() : null,
            p != null ? p.getId() : null,
            p != null ? p.getName() : null,
            p != null ? p.getEmail() : null,
            p != null ? p.getPhone() : null,
            p != null ? p.getAddress() : null,
            appt.getAppointmentTime(),
            appt.getStatus()
        );
    }
// 1. **Add @Service Annotation**:
//    - The `@Service` annotation is used to mark this class as a Spring service component. 
//    - It will be managed by Spring's container and used for business logic related to patients and appointments.
//    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
//    - Instruction: Ensure constructor injection is used for all the required dependencies.

// 3. **createPatient Method**:
//    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
//    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
//    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.

// 4. **getPatientAppointment Method**:
//    - Retrieves a list of appointments for a specific patient, based on their ID.
//    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
//    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
//    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.

// 5. **filterByCondition Method**:
//    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
//    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
//    - Converts the appointments into `AppointmentDTO` and returns them in the response.
//    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.

// 6. **filterByDoctor Method**:
//    - Filters appointments for a patient based on the doctor's name.
//    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
//    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.

// 7. **filterByDoctorAndCondition Method**:
//    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
//    - This method combines filtering by doctor name and appointment status (past or future).
//    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
//    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.

// 8. **getPatientDetails Method**:
//    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
//    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
//    - It returns the patient's information in the response body.
    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.

// 9. **Handling Exceptions and Errors**:
//    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
//    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

// 10. **Use of DTOs (Data Transfer Objects)**:
//    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
//    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.



}
