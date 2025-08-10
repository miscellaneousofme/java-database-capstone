package com.project.back_end.repo;

import com.project_back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find a doctor by their email.
     *
     * @param email the email address to search
     * @return Doctor entity or null if not found
     */
    Doctor findByEmail(String email);

    /**
     * Find doctors by partial name match (case-sensitive by default).
     * Uses JPQL LIKE with wildcards on both sides.
     *
     * @param name partial name to search for
     * @return list of matching doctors
     */
    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(String name);

    /**
     * Find doctors whose name contains the given term (case-insensitive)
     * and whose specialty matches exactly (case-insensitive).
     *
     * @param name      partial name to search
     * @param specialty specialty to match exactly
     * @return list of matching doctors
     */
    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.name) LIKE CONCAT('%', LOWER(:name), '%') " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    /**
     * Find doctors by specialty, ignoring case.
     *
     * @param specialty the specialty to search for
     * @return list of matching doctors
     */
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
