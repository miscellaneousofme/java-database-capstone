package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.DTO.Login;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * 1. Doctor Availability
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return new ResponseEntity(validation.getBody(), validation.getStatusCode());
        }
        try {
            List<String> availability = doctorService.getDoctorAvailability(doctorId, LocalDate.parse(date));
            return ResponseEntity.ok(Collections.singletonMap("availability", availability));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("message", "Internal error fetching availability"));
        }
    }

    /**
     * 2. Get all doctors
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        List<Doctor> doctors = doctorService.getDoctors();
        return ResponseEntity.ok(Collections.singletonMap("doctors", doctors));
    }

    /**
     * 3. Add New Doctor (admin only)
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody Doctor doctor, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        int res = doctorService.saveDoctor(doctor);
        if (res == 1) {
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(Collections.singletonMap("message", "Doctor added to db"));
        } else if (res == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body(Collections.singletonMap("message", "Doctor already exists"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("message", "Some internal error occurred"));
        }
    }

    /**
     * 4. Doctor Login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    /**
     * 5. Update Doctor (admin only)
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody Doctor doctor, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        int res = doctorService.updateDoctor(doctor);
        if (res == 1) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Doctor updated"));
        } else if (res == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Collections.singletonMap("message", "Doctor not found"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("message", "Some internal error occurred"));
        }
    }

    /**
     * 6. Delete Doctor (admin only)
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "admin");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        int res = doctorService.deleteDoctor(id);
        if (res == 1) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Doctor deleted successfully"));
        } else if (res == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Collections.singletonMap("message", "Doctor not found with id"));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("message", "Some internal error occurred"));
        }
    }

    /**
     * 7. Filter doctors by name, time, speciality
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        Map<String, Object> result = service.filterDoctor(
                "null".equalsIgnoreCase(name) ? null : name,
                "null".equalsIgnoreCase(speciality) ? null : speciality,
                "null".equalsIgnoreCase(time) ? null : time);
        return ResponseEntity.ok(result);
    }


// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.


}
