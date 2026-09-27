package com.example.internshipmanagement.repository;

import com.example.internshipmanagement.entity.Candidate;
import com.example.internshipmanagement.entity.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Candidate entities.
 * Spring Data JPA generates the implementation automatically based on
 * method names, so no custom query language is needed for these cases.
 */
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    boolean existsByEmailIgnoreCase(String email);

    List<Candidate> findByRoleIgnoreCase(String role);

    List<Candidate> findByStatus(CandidateStatus status);

    List<Candidate> findByRoleIgnoreCaseAndStatus(String role, CandidateStatus status);
}
