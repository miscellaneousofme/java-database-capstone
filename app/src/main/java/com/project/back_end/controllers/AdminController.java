
package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service; // central service layer
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {

    private final Service service;

    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    /**
     * Admin login endpoint.
     * Accepts admin credentials (username, password) and returns a JWT token if valid.
     *
     * @param admin Admin object containing login credentials
     * @return ResponseEntity with either a token or an error message
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }

// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to indicate that it's a REST controller, used to handle web requests and return JSON responses.
//    - Use `@RequestMapping("${api.path}admin")` to define a base path for all endpoints in this controller.
//    - This allows the use of an external property (`api.path`) for flexible configuration of endpoint paths.


// 2. Autowire Service Dependency:
//    - Use constructor injection to autowire the `Service` class.
//    - The service handles core logic related to admin validation and token checking.
//    - This promotes cleaner code and separation of concerns between the controller and business logic layer.


// 3. Define the `adminLogin` Method:
//    - Handles HTTP POST requests for admin login functionality.
//    - Accepts an `Admin` object in the request body, which contains login credentials.
//    - Delegates authentication logic to the `validateAdmin` method in the service layer.
//    - Returns a `ResponseEntity` with a `Map` containing login status or messages.



}

