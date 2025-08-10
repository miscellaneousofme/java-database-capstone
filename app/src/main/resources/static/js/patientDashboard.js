// app/src/main/resources/static/js/patientDashboard.js

import { createDoctorCard } from "../components/doctorCard.js";
import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors } from "../services/doctorServices.js";
import { patientSignup, patientLogin } from "../services/patientServices.js";
import { selectRole } from "../render.js"; // needed in loginPatient()

// ===== Load all doctors when page is ready =====
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  // Modal triggers
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) signupBtn.addEventListener("click", () => openModal("patientSignup"));

  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) loginBtn.addEventListener("click", () => openModal("patientLogin"));

  // Search & filter triggers
  document.getElementById("searchBar")?.addEventListener("input", filterDoctorsOnChange);
  document.getElementById("filterTime")?.addEventListener("change", filterDoctorsOnChange);
  document.getElementById("filterSpecialty")?.addEventListener("change", filterDoctorsOnChange);
});

// ===== Fetch and render all doctors =====
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to load doctors:", error);
    document.getElementById("content").innerHTML =
      "<p style='color:red;'>Failed to load doctors.</p>";
  }
}

// ===== Filter doctors based on search/time/specialty =====
async function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar")?.value.trim();
  const filterTimeValue = document.getElementById("filterTime")?.value;
  const filterSpecialtyValue = document.getElementById("filterSpecialty")?.value;

  const name = searchBar || null;
  const time = filterTimeValue || null;
  const specialty = filterSpecialtyValue || null;

  try {
    const response = await filterDoctors(name, time, specialty);
    const doctors = response.doctors || [];
    if (doctors.length > 0) {
      renderDoctorCards(doctors);
    } else {
      document.getElementById("content").innerHTML =
        "<p>No doctors found with the given filters.</p>";
    }
  } catch (error) {
    console.error("Failed to filter doctors:", error);
    alert("❌ An error occurred while filtering doctors.");
  }
}

// ===== Utility: Render doctor array to UI =====
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// ===== Patient Signup =====
window.signupPatient = async function () {
  try {
    const name = document.getElementById("name")?.value.trim();
    const email = document.getElementById("email")?.value.trim();
    const password = document.getElementById("password")?.value.trim();
    const phone = document.getElementById("phone")?.value.trim();
    const address = document.getElementById("address")?.value.trim();

    if (!name || !email || !password || !phone || !address) {
      alert("Please fill in all fields.");
      return;
    }

    const { success, message } = await patientSignup({
      name,
      email,
      password,
      phone,
      address
    });

    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    } else {
      alert(message);
    }
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

// ===== Patient Login =====
window.loginPatient = async function () {
  try {
    const email = document.getElementById("email")?.value.trim();
    const password = document.getElementById("password")?.value.trim();

    if (!email || !password) {
      alert("Please enter both email and password.");
      return;
    }

    const response = await patientLogin({ email, password });
    if (response.ok) {
      const result = await response.json();
      // Set role in storage and token
      selectRole("loggedPatient");
      localStorage.setItem("token", result.token);
      // Redirect to logged-in patient dashboard
      window.location.href = "/pages/loggedPatientDashboard.html";
    } else {
      alert("❌ Invalid credentials!");
    }
  } catch (error) {
    console.error("Error in loginPatient:", error);
    alert("❌ Failed to login. Please try again.");
  }
};
