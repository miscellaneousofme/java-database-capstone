package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * GET — Retrieve appointments for a doctor on a given date,
     * optionally filtered by patient name, token must belong to a doctor.
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        // validate token for doctor
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse; // return error if invalid token
        }

        try {
            Map<String, Object> resultMap =
                    appointmentService.getAppointment(patientName, LocalDate.parse(date), token);
            return ResponseEntity.ok(resultMap);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Failed to fetch appointments"));
        }
    }

    /**
     * POST — Book a new appointment (token must belong to a patient).
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        // validate token for patient
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        // validate appointment
        int validationResult = service.validateAppointment(appointment);
        if (validationResult == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Invalid doctor ID"));
        } else if (validationResult == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Selected time slot is not available"));
        }

        // proceed to booking
        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("message", "Appointment booked successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "Failed to book appointment"));
        }
    }

    /**
     * PUT — Update an existing appointment (token must belong to a patient).
     */
    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        // validate token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        return appointmentService.updateAppointment(appointment);
    }

    /**
     * DELETE — Cancel an existing appointment (token must belong to a patient).
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token) {

        // validate token
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        return appointmentService.cancelAppointment(id, token);
    }


// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller.
//    - Use `@RequestMapping("/appointments")` to set a base path for all appointment-related endpoints.
//    - This centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.


// 2. Autowire Dependencies:
//    - Inject `AppointmentService` for handling the business logic specific to appointments.
//    - Inject the general `Service` class, which provides shared functionality like token validation and appointment checks.


// 3. Define the `getAppointments` Method:
//    - Handles HTTP GET requests to fetch appointments based on date and patient name.
//    - Takes the appointment date, patient name, and token as path variables.
//    - First validates the token for role `"doctor"` using the `Service`.
//    - If the token is valid, returns appointments for the given patient on the specified date.
//    - If the token is invalid or expired, responds with the appropriate message and status code.


// 4. Define the `bookAppointment` Method:
//    - Handles HTTP POST requests to create a new appointment.
//    - Accepts a validated `Appointment` object in the request body and a token as a path variable.
//    - Validates the token for the `"patient"` role.
//    - Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).
//    - Returns success if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.


// 5. Define the `updateAppointment` Method:
//    - Handles HTTP PUT requests to modify an existing appointment.
//    - Accepts a validated `Appointment` object and a token as input.
//    - Validates the token for `"patient"` role.
//    - Delegates the update logic to the `AppointmentService`.
//    - Returns an appropriate success or failure response based on the update result.


// 6. Define the `cancelAppointment` Method:
//    - Handles HTTP DELETE requests to cancel a specific appointment.
//    - Accepts the appointment ID and a token as path variables.
//    - Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
//    - Calls `AppointmentService` to handle the cancellation process and returns the result.


}
