package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.CandidateRequest;
import com.example.internshipmanagement.dto.CandidateResponse;
import com.example.internshipmanagement.entity.CandidateStatus;
import com.example.internshipmanagement.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing the Candidate CRUD and search endpoints.
 * Contains no business logic - everything is delegated to CandidateService.
 */
@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<CandidateResponse> createCandidate(@Valid @RequestBody CandidateRequest request) {
        CandidateResponse response = candidateService.createCandidate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CandidateResponse>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    // IMPORTANT: this must be declared BEFORE /{id} would otherwise conflict,
    // but since Spring matches "/search" against the literal path segment
    // "search" only when it is not a numeric ID pattern, keeping this mapping
    // as its own distinct path (not /{id}) ensures GET /api/candidates/search
    // is routed here and never mistaken for a candidate ID lookup.
    @GetMapping("/search")
    public ResponseEntity<List<CandidateResponse>> searchCandidates(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) CandidateStatus status) {
        return ResponseEntity.ok(candidateService.searchCandidates(role, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateResponse> getCandidateById(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getCandidateById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateResponse> updateCandidate(
            @PathVariable Long id, @Valid @RequestBody CandidateRequest request) {
        return ResponseEntity.ok(candidateService.updateCandidate(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }
}
