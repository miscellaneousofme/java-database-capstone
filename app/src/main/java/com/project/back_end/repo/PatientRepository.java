package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Find a patient by their email address.
     *
     * @param email the email address to search for
     * @return the patient if found, otherwise null
     */
    Patient findByEmail(String email);

    /**
     * Find a patient by either their email or phone number.
     *
     * @param email the email to search for
     * @param phone the phone number to search for
     * @return the patient if found, otherwise null
     */
    Patient findByEmailOrPhone(String email, String phone);
}