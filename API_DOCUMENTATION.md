# API Documentation — Internship Management REST API

Base URL: `http://localhost:8080`

## Authentication

Every endpoint under `/api/candidates` requires **HTTP Basic
Authentication**.

```
Username: admin
Password: admin123
```

A request without credentials, or with incorrect credentials, receives:

```
HTTP/1.1 401 Unauthorized
```

```json
{
  "timestamp": "2026-09-27T12:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Authentication is required to access this resource"
}
```

---

## 1. Create Candidate

- **Method:** `POST`
- **URL:** `/api/candidates`
- **Authentication:** Required
- **Purpose:** Creates a new internship candidate.

### Request Body

```json
{
  "name": "Rahul Kumar",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "role": "Java Developer",
  "status": "APPLIED"
}
```

### Example Response — `201 CREATED`

```json
{
  "id": 1,
  "name": "Rahul Kumar",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "role": "Java Developer",
  "status": "APPLIED",
  "createdAt": "2026-09-27T12:00:00"
}
```

### Possible Errors

- `400 BAD REQUEST` — validation failure (blank name, invalid email, invalid phone, blank role, missing/invalid status)
- `401 UNAUTHORIZED` — missing or incorrect credentials
- `409 CONFLICT` — email already exists

---

## 2. Get All Candidates

- **Method:** `GET`
- **URL:** `/api/candidates`
- **Authentication:** Required
- **Purpose:** Returns every candidate in the system.

### Example Response — `200 OK`

```json
[
  {
    "id": 1,
    "name": "Rahul Kumar",
    "email": "rahul@gmail.com",
    "phone": "9876543210",
    "role": "Java Developer",
    "status": "APPLIED",
    "createdAt": "2026-09-27T12:00:00"
  }
]
```

### Possible Errors

- `401 UNAUTHORIZED`

---

## 3. Get Candidate By ID

- **Method:** `GET`
- **URL:** `/api/candidates/{id}`
- **Authentication:** Required
- **Purpose:** Returns a single candidate by ID.

### Example

`GET /api/candidates/1`

### Example Response — `200 OK`

```json
{
  "id": 1,
  "name": "Rahul Kumar",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "role": "Java Developer",
  "status": "APPLIED",
  "createdAt": "2026-09-27T12:00:00"
}
```

### Possible Errors

- `401 UNAUTHORIZED`
- `404 NOT FOUND`

```json
{
  "timestamp": "2026-09-27T12:30:00",
  "status": 404,
  "error": "Candidate Not Found",
  "message": "Candidate with id 10 not found"
}
```

---

## 4. Update Candidate

- **Method:** `PUT`
- **URL:** `/api/candidates/{id}`
- **Authentication:** Required
- **Purpose:** Updates an existing candidate's details. `id` and
  `createdAt` are preserved.

### Example

`PUT /api/candidates/1`

### Request Body

```json
{
  "name": "Rahul Kumar",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "role": "Backend Developer",
  "status": "SHORTLISTED"
}
```

### Example Response — `200 OK`

```json
{
  "id": 1,
  "name": "Rahul Kumar",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "role": "Backend Developer",
  "status": "SHORTLISTED",
  "createdAt": "2026-09-27T12:00:00"
}
```

### Possible Errors

- `400 BAD REQUEST` — validation failure
- `401 UNAUTHORIZED`
- `404 NOT FOUND` — candidate does not exist
- `409 CONFLICT` — new email belongs to another candidate

---

## 5. Delete Candidate

- **Method:** `DELETE`
- **URL:** `/api/candidates/{id}`
- **Authentication:** Required
- **Purpose:** Deletes a candidate.

### Example

`DELETE /api/candidates/1`

### Response — `204 NO CONTENT`

No response body.

### Possible Errors

- `401 UNAUTHORIZED`
- `404 NOT FOUND`

---

## 6. Search Candidates

- **Method:** `GET`
- **URL:** `/api/candidates/search`
- **Authentication:** Required
- **Purpose:** Searches candidates by role, status, or both. Role
  matching is case-insensitive. This endpoint is mapped separately from
  `/api/candidates/{id}` so `search` is never mistaken for a candidate ID.

### 6a. Search by role

`GET /api/candidates/search?role=Java Developer`

### 6b. Search by status

`GET /api/candidates/search?status=SHORTLISTED`

### 6c. Search by role and status

`GET /api/candidates/search?role=Java Developer&status=SHORTLISTED`

### Example Response — `200 OK`

```json
[
  {
    "id": 1,
    "name": "Rahul Kumar",
    "email": "rahul@gmail.com",
    "phone": "9876543210",
    "role": "Java Developer",
    "status": "SHORTLISTED",
    "createdAt": "2026-09-27T12:00:00"
  }
]
```

### Possible Errors

- `400 BAD REQUEST` — invalid `status` value
- `401 UNAUTHORIZED`

---

## Error Response Format

Every error returned by the API follows this structure:

```json
{
  "timestamp": "2026-09-27T12:30:00",
  "status": 404,
  "error": "Candidate Not Found",
  "message": "Candidate with id 10 not found"
}
```

## HTTP Status Codes Used

| Code | Meaning | Used for |
|------|---------|----------|
| 200 | OK | Successful GET / PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation errors, invalid enum values |
| 401 | Unauthorized | Missing/incorrect Basic Auth credentials |
| 404 | Not Found | Candidate does not exist |
| 409 | Conflict | Duplicate email |
| 500 | Internal Server Error | Unexpected server-side errors |
