package com.example.internshipmanagement.exception;

/**
 * Thrown when a candidate is created or updated with an email address
 * that already belongs to another candidate.
 * Handled by GlobalExceptionHandler and translated into a 409 response.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A candidate with this email already exists");
    }
}
