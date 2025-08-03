# Smart Clinic Management System – Database Schema Design

## MySQL Database Design

The clinic uses MySQL to store structured, relational core data with strict validation and relationships.

### Table: patients

| Column Name   | Data Type     | Constraints                           |
|---------------|--------------|---------------------------------------|
| patient_id    | INT          | PRIMARY KEY, AUTO_INCREMENT           |
| first_name    | VARCHAR(50)  | NOT NULL                              |
| last_name     | VARCHAR(50)  | NOT NULL                              |
| dob           | DATE         | NOT NULL                              |
| gender        | VARCHAR(20)  |                                       |
| email         | VARCHAR(100) | NOT NULL, UNIQUE                      |
| phone         | VARCHAR(20)  | UNIQUE                                |
| address       | VARCHAR(255) |                                       |
| registered_on | DATETIME     | DEFAULT CURRENT_TIMESTAMP             |

<!-- email and phone are unique for direct communication and account validation. -->

### Table: doctors

| Column Name     | Data Type     | Constraints                      |
|-----------------|--------------|----------------------------------|
| doctor_id       | INT          | PRIMARY KEY, AUTO_INCREMENT      |
| first_name      | VARCHAR(50)  | NOT NULL                         |
| last_name       | VARCHAR(50)  | NOT NULL                         |
| specialization  | VARCHAR(100) |                                  |
| email           | VARCHAR(100) | NOT NULL, UNIQUE                 |
| phone           | VARCHAR(20)  |                                  |
| available       | BOOLEAN      | DEFAULT true                     |
| created_at      | DATETIME     | DEFAULT CURRENT_TIMESTAMP        |

<!-- 'available' enables flexible scheduling and status updates. -->

### Table: appointments

| Column Name      | Data Type     | Constraints                                 |
|------------------|--------------|---------------------------------------------|
| appointment_id   | INT          | PRIMARY KEY, AUTO_INCREMENT                 |
| patient_id       | INT          | FOREIGN KEY → patients(patient_id), NOT NULL|
| doctor_id        | INT          | FOREIGN KEY → doctors(doctor_id), NOT NULL  |
| scheduled_time   | DATETIME     | NOT NULL                                    |
| status           | VARCHAR(20)  | DEFAULT 'scheduled'                         |
| notes            | TEXT         |                                             |
| created_at       | DATETIME     | DEFAULT CURRENT_TIMESTAMP                   |

<!-- Appointments must always reference an existing patient and doctor. -->

### Table: admin

| Column Name   | Data Type     | Constraints                        |
|--------------|--------------|------------------------------------|
| admin_id     | INT          | PRIMARY KEY, AUTO_INCREMENT        |
| username     | VARCHAR(50)  | NOT NULL, UNIQUE                   |
| password_hash| VARCHAR(255) | NOT NULL                           |
| email        | VARCHAR(100) | UNIQUE                             |
| role         | VARCHAR(30)  | NOT NULL                           |
| created_at   | DATETIME     | DEFAULT CURRENT_TIMESTAMP          |

<!-- password_hash uses a long field to fit secure bcrypt hashes. -->

---

## MongoDB Collection Design

For flexible, nested, or evolving data (such as prescriptions, doctor notes, or feedback), MongoDB stores information as documents.

### Collection: prescriptions


``json
{
"_id": "ObjectId('64acab3a3c09df8abc123456')",
"appointment_id": 3051,
"patient_id": 203,
"doctor_id": 12,
"issued_at": "2025-08-03T15:00:00Z",
"medications": [
{
"name": "Amoxicillin",
"dose": "500mg",
"route": "oral",
"frequency": "3 times/day",
"duration_days": 7
},
{
"name": "Ibuprofen",
"dose": "200mg",
"frequency": "as needed for pain",
"duration_days": 5
}
],
"doctor_notes": [
"Complete full antibiotic course.",
"Monitor for allergic reactions."
],
"pharmacy_info": {
"name": "Central Pharmacy",
"address": "123 Main St"
},
"refills_remaining": 1,
"tags": ["antibiotic", "pain relief"]
}


<!--
- appointment_id, patient_id, and doctor_id link to structured data.
- 'medications' is an array for multiple drugs.
- doctor_notes allow flexible entry, and tags aid filtering.
- Structure can evolve as more metadata is needed.
-->

---

**Design Notes:**  
- Use MySQL for structured, highly relational data (patients, doctors, admins, appointments).  
- Use MongoDB for flexible or variable data (prescriptions with medication lists, notes, etc.).  
- Each database is optimized for its strength, and referencing IDs connects the relational and document-based worlds.

---

