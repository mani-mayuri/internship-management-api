package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.CandidateRequest;
import com.example.internshipmanagement.dto.CandidateResponse;
import com.example.internshipmanagement.entity.CandidateStatus;

import java.util.List;

/**
 * Business logic contract for managing internship candidates.
 */
public interface CandidateService {

    CandidateResponse createCandidate(CandidateRequest request);

    List<CandidateResponse> getAllCandidates();

    CandidateResponse getCandidateById(Long id);

    CandidateResponse updateCandidate(Long id, CandidateRequest request);

    void deleteCandidate(Long id);

    List<CandidateResponse> searchCandidates(String role, CandidateStatus status);
}
