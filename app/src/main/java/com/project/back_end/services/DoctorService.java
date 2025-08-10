package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.DTO.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /** ------------------- Core Methods ------------------- **/

    /**
     * Find available slots for a doctor on a given date.
     */
    @Transactional(readOnly = true)
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        // Example: All possible slots
        List<String> allSlots = Arrays.asList(
                "09:00 AM", "10:00 AM", "11:00 AM",
                "12:00 PM", "01:00 PM", "02:00 PM",
                "03:00 PM", "04:00 PM", "05:00 PM"
        );

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        Set<String> bookedSlots = bookedAppointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toSet());

        // Filter out booked appointments
        return allSlots.stream()
                .filter(slot -> bookedSlots.stream().noneMatch(slot::contains))
                .collect(Collectors.toList());
    }

    /**
     * Save a new doctor.
     */
    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1; // Doctor already exists
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Internal error
        }
    }

    /**
     * Update an existing doctor.
     */
    @Transactional
    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Retrieve all doctors.
     */
    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Delete a doctor and their related appointments.
     */
    @Transactional
    public int deleteDoctor(long id) {
        try {
            if (doctorRepository.findById(id).isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Validate Doctor login.
     */
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        String token = tokenService.generateTokenForDoctor(doctor);
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    /**
     * Find doctors by partial name match.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("doctors", doctorRepository.findByNameLike(name));
        return result;
    }

    /** ------------------- Filtering Methods ------------------- **/

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        filtered = filterDoctorByTime(filtered, amOrPm);
        return Collections.singletonMap("doctors", filtered);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findByNameLike(name);
        filtered = filterDoctorByTime(filtered, amOrPm);
        return Collections.singletonMap("doctors", filtered);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        return Collections.singletonMap("doctors",
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        filtered = filterDoctorByTime(filtered, amOrPm);
        return Collections.singletonMap("doctors", filtered);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        return Collections.singletonMap("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> filtered = doctorRepository.findAll();
        filtered = filterDoctorByTime(filtered, amOrPm);
        return Collections.singletonMap("doctors", filtered);
    }

    /**
     * Private helper to filter doctors by AM/PM.
     */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isBlank()) return doctors;
        String filter = amOrPm.trim().toUpperCase();
        return doctors.stream()
                .filter(d -> d.getAvailableTimes()
                        .stream()
                        .anyMatch(t -> t.toUpperCase().contains(filter)))
                .collect(Collectors.toList());
    }
}
