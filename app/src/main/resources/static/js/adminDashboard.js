// app/src/main/resources/static/js/adminDashboard.js

import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";

// --- Load All Doctor Cards on Page Load ---
document.addEventListener("DOMContentLoaded", loadDoctorCards);

// Attach Add Doctor button event (after DOM has loaded!)
document.addEventListener("DOMContentLoaded", () => {
  const addDocBtn = document.getElementById("addDocBtn");
  if (addDocBtn) {
    addDocBtn.addEventListener("click", () => openModal("addDoctor"));
  }
});

// --- Search/Filter Event Binding ---
document.addEventListener("DOMContentLoaded", () => {
  const searchBar = document.getElementById("searchBar");
  const timeFilter = document.getElementById("filterTime");
  const specialtyFilter = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (timeFilter) timeFilter.addEventListener("change", filterDoctorsOnChange);
  if (specialtyFilter) specialtyFilter.addEventListener("change", filterDoctorsOnChange);
});

// --- Load all doctors and render their cards ---
async function loadDoctorCards() {
  try {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "<p>Loading doctors...</p>";
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctor cards:", error);
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "<p style='color:red;'>Failed to load doctors.</p>";
  }
}

// --- Filter/Search Doctors on input change ---
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar")?.value.trim() || "";
  const time = document.getElementById("filterTime")?.value || "";
  const specialty = document.getElementById("filterSpecialty")?.value || "";

  try {
    const doctors = await filterDoctors(name || "", time || "", specialty || "");
    renderDoctorCards(doctors);
    if (!doctors || doctors.length === 0) {
      document.getElementById("content").innerHTML = "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Something went wrong while filtering doctors.");
  }
}

// --- Helper: Render a list of doctors as cards ---
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML = "<p>No doctors found.</p>";
    return;
  }
  doctors.forEach(doctor => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// --- Add a Doctor: Collect modal form data and save doctor ---
window.adminAddDoctor = async function adminAddDoctor() {
  // Modal form field IDs
  const name = document.getElementById("doctorName")?.value.trim();
  const email = document.getElementById("doctorEmail")?.value.trim();
  const phone = document.getElementById("doctorPhone")?.value.trim();
  const password = document.getElementById("doctorPassword")?.value.trim();
  const specialty = document.getElementById("doctorSpecialty")?.value.trim();

  // Collect "availableTimes" from checkbox group (class/IDs to be set in your modal HTML)
  const timeCheckboxes = document.querySelectorAll(".doctorAvailableTime:checked");
  const availableTimes = Array.from(timeCheckboxes).map(cb => cb.value);

  if (!name || !email || !phone || !password || !specialty) {
    alert("Please fill in all required doctor details.");
    return;
  }

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Session expired. Please login again as admin.");
    window.location.href = "/";
    return;
  }

  // Build doctor object (match your backend fields)
  const doctor = {
    name,
    email,
    phone,
    password,
    specialty,
    availableTimes
  };

  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert("Doctor added successfully!");
      // Close modal if using a modal component
      document.getElementById("modal")?.classList.remove("active");
      // Optionally clear form fields here
      // Reload doctor cards
      await loadDoctorCards();
    } else {
      alert("Failed to add doctor: " + (result.message || "Unknown error"));
    }
  } catch (error) {
    console.error("Error adding doctor:", error);
    alert("Something went wrong while adding doctor.");
  }
};

// --- Export if needed ---
export {}; // If you're loading this file via <script type="module">, this helps avoid unintentional global leakage


/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
