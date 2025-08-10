// app/src/main/resources/static/js/services/patientServices.js

import { API_BASE_URL } from "../config/config.js";

// Base endpoint for all patient-related requests
const PATIENT_API = API_BASE_URL + '/patient';

/**
 * Register a new patient (Sign Up)
 * @param {Object} data - Patient info { name, email, password, phone, ... }
 * @returns {Promise<{success:boolean, message:string}>}
 */
export async function patientSignup(data) {
  try {
    const response = await fetch(`${PATIENT_API}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });
    const result = await response.json();
    if (!response.ok) throw new Error(result.message || "Signup failed");
    return { success: true, message: result.message };
  } catch (error) {
    console.error("Error :: patientSignup :: ", error);
    return { success: false, message: error.message || "Unexpected error during signup" };
  }
}

/**
 * Login a patient
 * @param {Object} data - { email, password }
 * @returns {Promise<Response>} - raw fetch response for caller to handle .ok, .json()
 * 
 * NOTE: We return the raw fetch Response so the UI can decide how to handle token etc.
 */
export async function patientLogin(data) {
  console.log("patientLogin :: ", data); // remove in production
  return await fetch(`${PATIENT_API}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data)
  });
}

/**
 * Fetch details of a logged-in patient
 * @param {string} token - Auth token from localStorage
 * @returns {Promise<Object|null>} - patient object or null if error
 */
export async function getPatientData(token) {
  try {
    const response = await fetch(`${PATIENT_API}/${token}`);
    const data = await response.json();
    if (response.ok) {
      return data.patient;
    }
    return null;
  } catch (error) {
    console.error("Error fetching patient details:", error);
    return null;
  }
}

/**
 * Fetch appointments for a patient (or patient record for doctor view)
 * Backend API is designed so same endpoint works for 'patient' or 'doctor' user types
 * @param {string|number} id - ID of the patient
 * @param {string} token - Auth token
 * @param {string} user - "patient" or "doctor"
 * @returns {Promise<Array|null>} - appointments array or null if error
 */
export async function getPatientAppointments(id, token, user) {
  try {
    const response = await fetch(`${PATIENT_API}/${id}/${user}/${token}`);
    const data = await response.json();
    console.log("Appointments:", data.appointments);
    if (response.ok) {
      return data.appointments;
    }
    return null;
  } catch (error) {
    console.error("Error fetching patient appointments:", error);
    return null;
  }
}

/**
 * Filter appointments based on condition and patient name
 * @param {string} condition - e.g. "pending", "consulted"
 * @param {string} name - patient name (or empty string for all)
 * @param {string} token - Auth token
 * @returns {Promise<{appointments:Array}>} - object with appointments array
 */
export async function filterAppointments(condition, name, token) {
  try {
    const response = await fetch(
      `${PATIENT_API}/filter/${condition}/${name}/${token}`,
      {
        method: "GET",
        headers: { "Content-Type": "application/json" }
      }
    );

    if (response.ok) {
      const data = await response.json();
      return data; // assuming API returns {appointments: [...]}
    } else {
      console.error("Failed to fetch appointments:", response.statusText);
      return { appointments: [] };
    }
  } catch (error) {
    console.error("Error filtering appointments:", error);
    alert("Something went wrong!");
    return { appointments: [] };
  }
}
