# Internship Management REST API

## Description

This is a simple Spring Boot REST API for managing internship candidates.
It lets a company (or an HR team) create candidate records, look them up,
update their status as they move through the hiring process, search them
by role or status, and delete them when no longer needed. Every candidate
API is protected with HTTP Basic Authentication.

## Problem Statement

A company running an internship program needs a small backend to keep
track of applicants: who applied, what role they applied for, and what
stage of the hiring pipeline they are currently in (applied, shortlisted,
interview, selected, rejected). This project implements that as a REST API
backed by a MySQL database.

## Features

- Candidate creation
- Candidate retrieval (all candidates / by ID)
- Candidate update
- Candidate deletion
- Role management (free-text role field)
- Candidate status management (fixed set of pipeline stages)
- Input validation (name, email, phone, role, status)
- MySQL database integration via Spring Data JPA
- Centralized exception handling with clean JSON error responses
- Search by role
- Search by status
- Search by role + status
- HTTP Basic Authentication on all candidate endpoints
- Duplicate email prevention

## Technologies

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security (HTTP Basic Authentication)
- MySQL
- Maven
- Jakarta Bean Validation
- JUnit 5
- Mockito
- Postman

## Architecture

```
Postman
   |
   v
Controller  (CandidateController)
   |
   v
Service     (CandidateService / CandidateServiceImpl)
   |
   v
Repository  (CandidateRepository)
   |
   v
MySQL
```

A simple layered architecture is used on purpose: the controller only
handles HTTP concerns, the service holds all business rules, and the
repository talks to the database through Spring Data JPA.

## Project Structure

```
com.example.internshipmanagement
│
├── controller       -> CandidateController (REST endpoints)
├── service           -> CandidateService, CandidateServiceImpl (business logic)
├── repository        -> CandidateRepository (Spring Data JPA)
├── entity             -> Candidate, CandidateStatus
├── dto                -> CandidateRequest, CandidateResponse
├── exception          -> CandidateNotFoundException, DuplicateEmailException,
│                          GlobalExceptionHandler, ErrorResponse
├── security           -> SecurityConfig, RestAuthenticationEntryPoint
└── InternshipManagementApplication.java
```

## Database Setup

1. Install MySQL locally (or use an existing instance).
2. Start the MySQL server.
3. Create the database:

   ```sql
   CREATE DATABASE internship_management;
   ```

4. Open `src/main/resources/application.properties` and set your own
   local MySQL username and password.
5. Start the Spring Boot application. Spring Data JPA will automatically
   create the `candidates` table on startup (`spring.jpa.hibernate.ddl-auto=update`).

## Configuration

`application.properties` ships with a placeholder:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

Replace `YOUR_PASSWORD` with your own local MySQL password before running
the application. **No real password is committed to this repository.**
If you prefer, you can instead export the values as environment variables
and reference them with `${DB_USERNAME}` / `${DB_PASSWORD}` placeholders.

## Authentication

All `/api/candidates/**` endpoints require HTTP Basic Authentication.

```
Username: admin
Password: admin123
```

These are local demonstration credentials only, defined in-memory in
`SecurityConfig`. They are not meant for production use.

## How to Run

### Eclipse

Import the project as an existing Maven project, then run
`InternshipManagementApplication.java` as a Java application.

### IntelliJ IDEA

Open the folder as a Maven project and run the
`InternshipManagementApplication` main class.

### Maven (command line)

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/candidates` | Create a new candidate |
| GET    | `/api/candidates` | Get all candidates |
| GET    | `/api/candidates/{id}` | Get a candidate by ID |
| PUT    | `/api/candidates/{id}` | Update a candidate |
| DELETE | `/api/candidates/{id}` | Delete a candidate |
| GET    | `/api/candidates/search?role=...` | Search by role |
| GET    | `/api/candidates/search?status=...` | Search by status |
| GET    | `/api/candidates/search?role=...&status=...` | Search by role and status |

See `API_DOCUMENTATION.md` for full request/response examples.

## Sample JSON

Create request:

```json
{
  "name": "Rahul Kumar",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "role": "Java Developer",
  "status": "APPLIED"
}
```

Update request:

```json
{
  "name": "Rahul Kumar",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "role": "Backend Developer",
  "status": "SHORTLISTED"
}
```

## Validation

| Field  | Rule |
|--------|------|
| name   | Cannot be blank |
| email  | Cannot be blank, must be a valid email address |
| phone  | Cannot be blank, must be 10–15 digits |
| role   | Cannot be blank |
| status | Cannot be null, must be one of `APPLIED`, `SHORTLISTED`, `INTERVIEW`, `SELECTED`, `REJECTED` |

Invalid input returns `400 BAD REQUEST` with a clean JSON error body
(no stack traces).

## Error Handling

`GlobalExceptionHandler` (a `@RestControllerAdvice`) converts every
exception into a consistent JSON structure:

- `CandidateNotFoundException` → `404 NOT FOUND`
- `DuplicateEmailException` → `409 CONFLICT`
- Bean Validation failures → `400 BAD REQUEST`
- Invalid enum / type mismatch (e.g. bad `status` value) → `400 BAD REQUEST`
- Authentication failures → `401 UNAUTHORIZED`
- Any other unexpected error → `500 INTERNAL SERVER ERROR`

No stack traces, database details, or internal implementation details are
ever returned to the client.

## Postman

Import `Internship-Management-API.postman_collection.json` into Postman.
It uses a `baseUrl` variable (`http://localhost:8080`) and a `candidateId`
variable that gets set automatically after creating a candidate, plus
Basic Authentication configured with the demo credentials
(`admin` / `admin123`).

## Testing

Run all tests with:

```bash
mvn test
```

Tests use an in-memory H2 database (configured in
`src/test/resources/application.properties`), so no MySQL instance is
required to run them. They cover the service layer (Mockito), validation,
search, duplicate-email handling, and Basic Authentication (via MockMvc
integration tests).

## Future Improvements

These are ideas for later — none of them are implemented in this version:

- Pagination for `GET /api/candidates`
- JWT authentication
- Role-based authorization (e.g. admin vs. viewer)
- Swagger / OpenAPI documentation
- Candidate interview scheduling
