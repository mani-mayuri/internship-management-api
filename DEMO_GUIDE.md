# Demo Guide — Internship Management REST API

A 5–10 minute walkthrough you can follow live in an interview.

## Suggested Flow

1. **Start MySQL** locally.
2. **Create the database:**
   ```sql
   CREATE DATABASE internship_management;
   ```
3. **Start the Spring Boot application** (`mvn spring-boot:run`, or run
   `InternshipManagementApplication` from your IDE).
4. **Show successful startup** — point out the log line showing the app
   is running on port 8080 and Hibernate creating the `candidates` table.
5. **Open Postman** and import `Internship-Management-API.postman_collection.json`.
6. **Show an unauthenticated request → 401**
   `GET /api/candidates` with no credentials.
7. **Add Basic Authentication** (`admin` / `admin123`) in Postman.
8. **Create a candidate → 201**
   `POST /api/candidates` with the sample JSON body.
9. **Show the candidate row in MySQL** (`SELECT * FROM candidates;`).
10. **Get all candidates → 200**
11. **Get candidate by ID → 200**
12. **Update the candidate → 200** (change role/status).
13. **Search by role**
14. **Search by status**
15. **Search by role + status**
16. **Test invalid email → 400**
17. **Test invalid phone → 400**
18. **Test missing field → 400**
19. **Test invalid status value → 400**
20. **Test duplicate email → 409** (create the same candidate again).
21. **Test candidate not found → 404** (`GET /api/candidates/99999`).
22. **Delete the candidate → 204**
23. **Show the database again** to confirm the row is gone.

## Talking Script

You can say something close to this while demoing:

> "I built this as an Internship Management REST API. It uses a layered
> architecture with Controller, Service and Repository layers, so the
> controller only deals with HTTP and all the business rules — like
> checking for a duplicate email — live in the service layer. Spring Data
> JPA handles the database operations against MySQL, and I used Jakarta
> Bean Validation to validate incoming requests. All errors go through a
> single global exception handler so the API always returns clean,
> consistent JSON instead of a stack trace. As bonus features I added
> search by role and status, and HTTP Basic Authentication using Spring
> Security so the candidate endpoints aren't open to anyone."

Keep the tone conversational — the goal is to show you understand what
each layer does and why, not to read the script word for word.
