package com.example.internshipmanagement.exception;

/**
 * Thrown when a candidate cannot be found by the given ID.
 * Handled by GlobalExceptionHandler and translated into a 404 response.
 */
public class CandidateNotFoundException extends RuntimeException {

    public CandidateNotFoundException(Long id) {
        super("Candidate with id " + id + " not found");
    }
}
