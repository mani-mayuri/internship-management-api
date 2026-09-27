package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.CandidateRequest;
import com.example.internshipmanagement.dto.CandidateResponse;
import com.example.internshipmanagement.entity.Candidate;
import com.example.internshipmanagement.entity.CandidateStatus;
import com.example.internshipmanagement.exception.CandidateNotFoundException;
import com.example.internshipmanagement.exception.DuplicateEmailException;
import com.example.internshipmanagement.repository.CandidateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implements the candidate business logic on top of CandidateRepository.
 * Converts between the API-facing DTOs and the Candidate entity manually
 * (no MapStruct), which keeps the mapping easy to read and explain.
 */
@Service
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateServiceImpl(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    @Override
    public CandidateResponse createCandidate(CandidateRequest request) {
        if (candidateRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        Candidate candidate = new Candidate(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getRole(),
                request.getStatus());

        Candidate saved = candidateRepository.save(candidate);
        return toResponse(saved);
    }

    @Override
    public List<CandidateResponse> getAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CandidateResponse getCandidateById(Long id) {
        Candidate candidate = findCandidateOrThrow(id);
        return toResponse(candidate);
    }

    @Override
    public CandidateResponse updateCandidate(Long id, CandidateRequest request) {
        Candidate candidate = findCandidateOrThrow(id);

        boolean emailChanged = !candidate.getEmail().equalsIgnoreCase(request.getEmail());
        if (emailChanged && candidateRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        candidate.setName(request.getName());
        candidate.setEmail(request.getEmail());
        candidate.setPhone(request.getPhone());
        candidate.setRole(request.getRole());
        candidate.setStatus(request.getStatus());
        // id and createdAt are intentionally left untouched

        Candidate updated = candidateRepository.save(candidate);
        return toResponse(updated);
    }

    @Override
    public void deleteCandidate(Long id) {
        Candidate candidate = findCandidateOrThrow(id);
        candidateRepository.delete(candidate);
    }

    @Override
    public List<CandidateResponse> searchCandidates(String role, CandidateStatus status) {
        List<Candidate> results;

        boolean hasRole = role != null && !role.isBlank();
        boolean hasStatus = status != null;

        if (hasRole && hasStatus) {
            results = candidateRepository.findByRoleIgnoreCaseAndStatus(role, status);
        } else if (hasRole) {
            results = candidateRepository.findByRoleIgnoreCase(role);
        } else if (hasStatus) {
            results = candidateRepository.findByStatus(status);
        } else {
            results = candidateRepository.findAll();
        }

        return results.stream().map(this::toResponse).toList();
    }

    private Candidate findCandidateOrThrow(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new CandidateNotFoundException(id));
    }

    private CandidateResponse toResponse(Candidate candidate) {
        return new CandidateResponse(
                candidate.getId(),
                candidate.getName(),
                candidate.getEmail(),
                candidate.getPhone(),
                candidate.getRole(),
                candidate.getStatus(),
                candidate.getCreatedAt());
    }
}
