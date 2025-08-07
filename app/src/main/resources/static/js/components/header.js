// header.js

// Function to open modals based on type (placeholder, you need to implement this)
function openModal(type) {
    // Example implementation
    // This should show the appropriate modal based on 'type'
    // For now, just a console log or basic modal trigger. Replace with real logic.
    console.log(`Open modal: ${type}`);
    // Example: document.getElementById('modal').style.display = 'block';
  }
  
  // Render the header based on user role and session
  function renderHeader() {
    const headerDiv = document.getElementById("header");
  
    // If on home page ("/"), clear session and render basic header
    if (window.location.pathname.endsWith("/")) {
      localStorage.removeItem("userRole");
      localStorage.removeItem("token");
      headerDiv.innerHTML = `
        <header class="header">
          <div class="logo-section">
            <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
            <span class="logo-title">Hospital CMS</span>
          </div>
        </header>`;
      return;
    }
  
    // Get user role and token from localStorage
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");
  
    // Initialize header content with logo and nav container
    let headerContent = `
      <header class="header">
        <div class="logo-section">
          <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
          <span class="logo-title">Hospital CMS</span>
        </div>
        <nav>`;
  
    // Handle session expired or no token when logged in
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
      localStorage.removeItem("userRole");
      alert("Session expired or invalid login. Please log in again.");
      window.location.href = "/";
      return;
    }
  
    // Append role-specific header controls
    if (role === "admin") {
      headerContent += `
        <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
        <a href="#" id="logoutLink">Logout</a>`;
    } else if (role === "doctor") {
      // Home button leads to doctor dashboard or home page
      headerContent += `
        <button id="doctorHomeBtn" class="adminBtn" onclick="selectRole('doctor')">Home</button>
        <a href="#" id="logoutLink">Logout</a>`;
    } else if (role === "patient") {
      headerContent += `
        <button id="patientLogin" class="adminBtn">Login</button>
        <button id="patientSignup" class="adminBtn">Sign Up</button>`;
    } else if (role === "loggedPatient") {
      headerContent += `
        <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
        <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
        <a href="#" id="logoutPatientLink">Logout</a>`;
    }
  
    // Close nav and header tags
    headerContent += `</nav></header>`;
  
    // Inject header HTML
    headerDiv.innerHTML = headerContent;
  
    // Attach event listeners to buttons dynamically created
    attachHeaderButtonListeners();
  }
  
  // Attach event listeners to header buttons to handle clicks
  function attachHeaderButtonListeners() {
    // Add Doctor button (admin)
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
      addDocBtn.addEventListener("click", () => openModal("addDoctor"));
    }
  
    // Logout link (admin, doctor)
    const logoutLink = document.getElementById("logoutLink");
    if (logoutLink) {
      logoutLink.addEventListener("click", (e) => {
        e.preventDefault();
        logout();
      });
    }
  
    // Logout link (loggedPatient)
    const logoutPatientLink = document.getElementById("logoutPatientLink");
    if (logoutPatientLink) {
      logoutPatientLink.addEventListener("click", (e) => {
        e.preventDefault();
        logoutPatient();
      });
    }
  
    // Patient Login button
    const patientLogin = document.getElementById("patientLogin");
    if (patientLogin) {
      patientLogin.addEventListener("click", () => openModal("patientLogin"));
    }
  
    // Patient Signup button
    const patientSignup = document.getElementById("patientSignup");
    if (patientSignup) {
      patientSignup.addEventListener("click", () => openModal("patientSignup"));
    }
  
    // Doctor Home button
    const doctorHomeBtn = document.getElementById("doctorHomeBtn");
    if (doctorHomeBtn) {
      doctorHomeBtn.addEventListener("click", () => selectRole("doctor"));
    }
  
    // Home button for loggedPatient and other roles if needed can be added similarly
  }
  
  // Logout function removes token and role, redirect homepage
  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
  }
  
  // Logout for loggedPatient — resets to 'patient' role and removes token, then redirect to patient dashboard
  function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/pages/patientDashboard.html";
  }
  
  // Function to change role, e.g. used by doctor "Home" button
  function selectRole(role) {
    localStorage.setItem("userRole", role);
    // Redirect to corresponding page; example:
    if (role === "doctor") {
      window.location.href = "/pages/doctorDashboard.html";
    } else if (role === "admin") {
      window.location.href = "/pages/adminDashboard.html";
    } else if (role === "patient") {
      window.location.href = "/pages/patientDashboard.html";
    }
  }
  
  // Initialize header rendering on page load
  document.addEventListener("DOMContentLoaded", renderHeader);
  

/*
  Step-by-Step Explanation of Header Section Rendering

  This code dynamically renders the header section of the page based on the user's role, session status, and available actions (such as login, logout, or role-switching).

  1. Define the `renderHeader` Function

     * The `renderHeader` function is responsible for rendering the entire header based on the user's session, role, and whether they are logged in.

  2. Select the Header Div

     * The `headerDiv` variable retrieves the HTML element with the ID `header`, where the header content will be inserted.
       ```javascript
       const headerDiv = document.getElementById("header");
       ```

  3. Check if the Current Page is the Root Page

     * The `window.location.pathname` is checked to see if the current page is the root (`/`). If true, the user's session data (role) is removed from `localStorage`, and the header is rendered without any user-specific elements (just the logo and site title).
       ```javascript
       if (window.location.pathname.endsWith("/")) {
         localStorage.removeItem("userRole");
         headerDiv.innerHTML = `
           <header class="header">
             <div class="logo-section">
               <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
               <span class="logo-title">Hospital CMS</span>
             </div>
           </header>`;
         return;
       }
       ```

  4. Retrieve the User's Role and Token from LocalStorage

     * The `role` (user role like admin, patient, doctor) and `token` (authentication token) are retrieved from `localStorage` to determine the user's current session.
       ```javascript
       const role = localStorage.getItem("userRole");
       const token = localStorage.getItem("token");
       ```

  5. Initialize Header Content

     * The `headerContent` variable is initialized with basic header HTML (logo section), to which additional elements will be added based on the user's role.
       ```javascript
       let headerContent = `<header class="header">
         <div class="logo-section">
           <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
           <span class="logo-title">Hospital CMS</span>
         </div>
         <nav>`;
       ```

  6. Handle Session Expiry or Invalid Login

     * If a user with a role like `loggedPatient`, `admin`, or `doctor` does not have a valid `token`, the session is considered expired or invalid. The user is logged out, and a message is shown.
       ```javascript
       if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
         localStorage.removeItem("userRole");
         alert("Session expired or invalid login. Please log in again.");
         window.location.href = "/";   or a specific login page
         return;
       }
       ```

  7. Add Role-Specific Header Content

     * Depending on the user's role, different actions or buttons are rendered in the header:
       - **Admin**: Can add a doctor and log out.
       - **Doctor**: Has a home button and log out.
       - **Patient**: Shows login and signup buttons.
       - **LoggedPatient**: Has home, appointments, and logout options.
       ```javascript
       else if (role === "admin") {
         headerContent += `
           <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "doctor") {
         headerContent += `
           <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "patient") {
         headerContent += `
           <button id="patientLogin" class="adminBtn">Login</button>
           <button id="patientSignup" class="adminBtn">Sign Up</button>`;
       } else if (role === "loggedPatient") {
         headerContent += `
           <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <a href="#" onclick="logoutPatient()">Logout</a>`;
       }
       ```



  9. Close the Header Section



  10. Render the Header Content

     * Insert the dynamically generated `headerContent` into the `headerDiv` element.
       ```javascript
       headerDiv.innerHTML = headerContent;
       ```

  11. Attach Event Listeners to Header Buttons

     * Call `attachHeaderButtonListeners` to add event listeners to any dynamically created buttons in the header (e.g., login, logout, home).
       ```javascript
       attachHeaderButtonListeners();
       ```


  ### Helper Functions

  13. **attachHeaderButtonListeners**: Adds event listeners to login buttons for "Doctor" and "Admin" roles. If clicked, it opens the respective login modal.

  14. **logout**: Removes user session data and redirects the user to the root page.

  15. **logoutPatient**: Removes the patient's session token and redirects to the patient dashboard.

  16. **Render the Header**: Finally, the `renderHeader()` function is called to initialize the header rendering process when the page loads.
*/
   
