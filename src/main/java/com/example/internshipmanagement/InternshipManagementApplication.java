package com.example.internshipmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Internship Management REST API.
 *
 * This is a simple Spring Boot application that manages internship
 * candidates: creating, reading, updating, deleting and searching them.
 */
@SpringBootApplication
public class InternshipManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(InternshipManagementApplication.class, args);
    }
}
