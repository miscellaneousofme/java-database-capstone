Section 1: Architecture Summary

This Spring Boot application demonstrates a clean, multi-layered architecture by supporting both traditional web interfaces and modern API-based integrations. Thymeleaf templates are utilized to render dynamic, server-generated HTML for the Admin and Doctor dashboards, creating an interactive experience directly in the browser. REST APIs power external modules—such as Appointments, PatientDashboard, and PatientRecord—enabling programmatic access to data for mobile and web clients.

The application leverages two databases to optimize data management: MySQL is used to store structured, relational entities, such as patients, doctors, appointments, and administrators, while MongoDB manages unstructured, document-oriented records, including prescriptions. All incoming requests, whether via MVC or REST, are routed through a centralized service layer that encapsulates business logic and orchestrates workflows. This service layer interacts with dedicated repository interfaces, which abstract away database operations. Data from MySQL is handled as JPA entities, and MongoDB data as document models, providing a seamless and consistent programming model.

Section 2: Numbered Flow of Data and Control

1. The user interfaces with the system through either a Thymeleaf-powered dashboard (Admin or Doctor) or through RESTful API clients like Appointments, PatientDashboard, or PatientRecord.

2. The user's action—such as submitting a form or making an API request—is routed to the appropriate backend controller: Thymeleaf controllers handle web view requests, while REST controllers process API requests and return JSON data.

3. The controller delegates all business logic, validations, and workflow coordination to the service layer to maintain a clear separation of concerns.

4. The service layer consults one of two types of repositories: MySQL repositories (using JPA) for core structured data, or MongoDB repositories for flexible, document-oriented data.

5. Each repository performs direct access to its database: MySQL repositories interact with normalized relational tables, while MongoDB repositories communicate with collections of documents.

6. Retrieved or persisted data is mapped to Java model classes (entities for MySQL with @Entity annotation and document models for MongoDB with @Document annotation), ensuring an object-oriented data representation across layers.

7. These models are then used to generate the final response: Thymeleaf controllers pass them to HTML templates for dynamic web rendering, while REST controllers serialize them as JSON for client consumption, completing the request-response cycle.
