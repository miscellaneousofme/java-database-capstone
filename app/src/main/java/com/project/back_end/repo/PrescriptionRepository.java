package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PrescriptionRepository for MongoDB-based prescription data.
 * Extends MongoRepository for CRUD operations and includes a custom finder.
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    /**
     * Finds all prescriptions for a given appointment ID.
     *
     * @param appointmentId the ID of the appointment
     * @return list of matching prescriptions
     */
    List<Prescription> findByAppointmentId(Long appointmentId);
}
