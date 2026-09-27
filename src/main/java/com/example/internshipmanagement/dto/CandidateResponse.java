package com.example.internshipmanagement.dto;

import com.example.internshipmanagement.entity.CandidateStatus;
import java.time.LocalDateTime;

/**
 * Response body returned to the client for candidate data.
 */
public class CandidateResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private CandidateStatus status;
    private LocalDateTime createdAt;

    public CandidateResponse() {
    }

    public CandidateResponse(Long id, String name, String email, String phone,
                              String role, CandidateStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
