// doctorCard.js

import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { showBookingOverlay } from "../loggedPatient.js"; // Assumes this function shows booking UI overlay

/**
 * Creates a DOM element representing a doctor card.
 * @param {Object} doctor - Doctor info object with fields: name, specialty, email, availableTimes
 * @returns {HTMLElement} - The populated doctor card element
 */
export function createDoctorCard(doctor) {
  // Main card container
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Fetch current user role from localStorage
  const role = localStorage.getItem("userRole");

  // Doctor info container
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  // Doctor Name
  const name = document.createElement("h3");
  name.textContent = doctor.name;
  infoDiv.appendChild(name);

  // Specialization
  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialty}`;
  infoDiv.appendChild(specialization);

  // Email
  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;
  infoDiv.appendChild(email);

  // Available times (join array to string)
  const availability = document.createElement("p");
  availability.textContent = `Available Times: ${doctor.availableTimes ? doctor.availableTimes.join(", ") : "N/A"}`;
  infoDiv.appendChild(availability);

  // Actions container for buttons
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // Role-based controls:

  // Admin: show Delete button
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("adminBtn");
    removeBtn.addEventListener("click", async () => {
      if (!confirm(`Are you sure you want to delete Dr. ${doctor.name}?`)) return;
      const token = localStorage.getItem("token");
      if (!token) {
        alert("You must be logged in as admin to delete.");
        return;
      }
      try {
        const result = await deleteDoctor(doctor.id, token);
        if (result.success) {
          alert(`Doctor ${doctor.name} deleted successfully.`);
          card.remove(); // Remove card from DOM
        } else {
          alert(`Failed to delete doctor: ${result.message || "Unknown error"}`);
        }
      } catch (error) {
        console.error("Error deleting doctor:", error);
        alert("An error occurred while deleting the doctor.");
      }
    });
    actionsDiv.appendChild(removeBtn);

  } 
  // Patient not logged in: Book Now shows alert to login
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", () => {
      alert("Patient needs to login first.");
    });
    actionsDiv.appendChild(bookNow);

  }
  // Logged-in patient: Book Now shows booking overlay
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("You must be logged in to book an appointment.");
        window.location.href = "/"; // Or redirect to login page
        return;
      }
      try {
        const patientData = await getPatientData(token);
        // showBookingOverlay is assumed imported from loggedPatient.js
        showBookingOverlay(e, doctor, patientData);
      } catch (error) {
        console.error("Error fetching patient data:", error);
        alert("Could not retrieve patient info for booking.");
      }
    });
    actionsDiv.appendChild(bookNow);

  }
  // For other roles or no role - no action buttons displayed.

  // Append info and actions to main card container
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}


/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/
