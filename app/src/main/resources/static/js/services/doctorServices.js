// app/src/main/resources/static/js/services/doctorServices.js

import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

/**
 * Fetch all doctors
 * @returns {Promise<Array>} - Array of doctors or empty array if error
 */
export async function getDoctors() {
  try {
    const res = await fetch(DOCTOR_API, { method: "GET" });
    if (res.ok) {
      const data = await res.json();
      return data.doctors || [];
    } else {
      console.error("Failed to fetch doctors:", res.status);
      return [];
    }
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

/**
 * Delete a doctor (admin only)
 * @param {string|number} id - Doctor ID
 * @param {string} token - Admin authentication token
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
  try {
    const res = await fetch(`${DOCTOR_API}/${id}/${token}`, {
      method: "DELETE",
    });

    const data = await res.json();
    if (res.ok) {
      return { success: true, message: data.message || "Doctor deleted." };
    } else {
      return { success: false, message: data.message || "Failed to delete doctor." };
    }
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return { success: false, message: "Server error while deleting doctor." };
  }
}

/**
 * Save/Add a new doctor (admin only)
 * @param {Object} doctor - Doctor details
 * @param {string} token - Admin authentication token
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function saveDoctor(doctor, token) {
  try {
    const res = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    const data = await res.json();
    if (res.ok) {
      return { success: true, message: data.message || "Doctor saved successfully." };
    } else {
      return { success: false, message: data.message || "Failed to save doctor." };
    }
  } catch (error) {
    console.error("Error saving doctor:", error);
    return { success: false, message: "Server error while saving doctor." };
  }
}

/**
 * Filter doctors by name, time, and specialty
 * @param {string} name 
 * @param {string} time 
 * @param {string} specialty 
 * @returns {Promise<Array>} - Array of matching doctors
 */
export async function filterDoctors(name = "", time = "", specialty = "") {
  try {
    const res = await fetch(
      `${DOCTOR_API}/filter/${name || null}/${time || null}/${specialty || null}`,
      { method: "GET" }
    );

    if (res.ok) {
      const data = await res.json();
      return data.doctors || [];
    } else {
      console.error("Failed to filter doctors:", res.status);
      return [];
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("An error occurred while filtering doctors. Please try again.");
    return [];
  }
}


/*
  Import the base API URL from the config file
  Define a constant DOCTOR_API to hold the full endpoint for doctor-related actions


  Function: getDoctors
  Purpose: Fetch the list of all doctors from the API

   Use fetch() to send a GET request to the DOCTOR_API endpoint
   Convert the response to JSON
   Return the 'doctors' array from the response
   If there's an error (e.g., network issue), log it and return an empty array


  Function: deleteDoctor
  Purpose: Delete a specific doctor using their ID and an authentication token

   Use fetch() with the DELETE method
    - The URL includes the doctor ID and token as path parameters
   Convert the response to JSON
   Return an object with:
    - success: true if deletion was successful
    - message: message from the server
   If an error occurs, log it and return a default failure response


  Function: saveDoctor
  Purpose: Save (create) a new doctor using a POST request

   Use fetch() with the POST method
    - URL includes the token in the path
    - Set headers to specify JSON content type
    - Convert the doctor object to JSON in the request body

   Parse the JSON response and return:
    - success: whether the request succeeded
    - message: from the server

   Catch and log errors
    - Return a failure response if an error occurs


  Function: filterDoctors
  Purpose: Fetch doctors based on filtering criteria (name, time, and specialty)

   Use fetch() with the GET method
    - Include the name, time, and specialty as URL path parameters
   Check if the response is OK
    - If yes, parse and return the doctor data
    - If no, log the error and return an object with an empty 'doctors' array

   Catch any other errors, alert the user, and return a default empty result
*/
