package com.project.back_end.DTO;


/**
 * DTO for receiving login credentials from the client.
 * This is used in controller @RequestBody parameters for authentication.
 * It is NOT an entity and is not persisted in the database.
 */

public class Login {
   
    
    /**
     * The unique identifier for the user attempting to log in.
     * For Admin: username.
     * For Doctor/Patient: email.
     */
    private String identifier;

    /**
     * The password provided by the user.
     * This should match the stored (hashed) password for authentication to succeed.
     */
    private String password;

    // ===== Default Constructor =====
    public Login() {
    }

    // ===== All-Args Constructor (optional for convenience) =====
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // ===== Getters and Setters =====
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
// 1. 'email' field:
//    - Type: private String
//    - Description:
//      - Represents the email address used for logging into the system.
//      - The email field is expected to contain a valid email address for user authentication purposes.

// 2. 'password' field:
//    - Type: private String
//    - Description:
//      - Represents the password associated with the email address.
//      - The password field is used for verifying the user's identity during login.
//      - It is generally hashed before being stored and compared during authentication.

// 3. Constructor:
//    - No explicit constructor is defined for this class, as it relies on the default constructor provided by Java.
//    - This class can be initialized with setters or directly via reflection, as per the application's needs.

// 4. Getters and Setters:
//    - Standard getter and setter methods are provided for both 'email' and 'password' fields.
//    - The 'getEmail()' method allows access to the email value.
//    - The 'setEmail(String email)' method sets the email value.
//    - The 'getPassword()' method allows access to the password value.
//    - The 'setPassword(String password)' method sets the password value.


}
