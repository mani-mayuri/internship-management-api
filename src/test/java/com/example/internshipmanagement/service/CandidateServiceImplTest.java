package com.example.internshipmanagement.service;

import com.example.internshipmanagement.dto.CandidateRequest;
import com.example.internshipmanagement.dto.CandidateResponse;
import com.example.internshipmanagement.entity.Candidate;
import com.example.internshipmanagement.entity.CandidateStatus;
import com.example.internshipmanagement.exception.CandidateNotFoundException;
import com.example.internshipmanagement.exception.DuplicateEmailException;
import com.example.internshipmanagement.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CandidateServiceImpl. The repository is mocked so these
 * tests run fast and do not require a database.
 */
@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    private CandidateRequest request;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        request = new CandidateRequest();
        request.setName("Rahul Kumar");
        request.setEmail("rahul@gmail.com");
        request.setPhone("9876543210");
        request.setRole("Java Developer");
        request.setStatus(CandidateStatus.APPLIED);

        candidate = new Candidate("Rahul Kumar", "rahul@gmail.com", "9876543210",
                "Java Developer", CandidateStatus.APPLIED);
        candidate.setId(1L);
        candidate.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createCandidate_savesAndReturnsResponse_whenEmailIsUnique() {
        when(candidateRepository.existsByEmailIgnoreCase("rahul@gmail.com")).thenReturn(false);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        CandidateResponse response = candidateService.createCandidate(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("rahul@gmail.com");
        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void createCandidate_throwsDuplicateEmailException_whenEmailAlreadyExists() {
        when(candidateRepository.existsByEmailIgnoreCase("rahul@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> candidateService.createCandidate(request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(candidateRepository, never()).save(any(Candidate.class));
    }

    @Test
    void getCandidateById_returnsCandidate_whenFound() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        CandidateResponse response = candidateService.getCandidateById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Rahul Kumar");
    }

    @Test
    void getCandidateById_throwsNotFoundException_whenMissing() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.getCandidateById(99L))
                .isInstanceOf(CandidateNotFoundException.class);
    }

    @Test
    void deleteCandidate_deletesCandidate_whenFound() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        candidateService.deleteCandidate(1L);

        verify(candidateRepository).delete(candidate);
    }

    @Test
    void deleteCandidate_throwsNotFoundException_whenMissing() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.deleteCandidate(99L))
                .isInstanceOf(CandidateNotFoundException.class);

        verify(candidateRepository, never()).delete(any(Candidate.class));
    }

    @Test
    void updateCandidate_updatesFields_whenEmailUnchanged() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        request.setRole("Backend Developer");
        request.setStatus(CandidateStatus.SHORTLISTED);
        // same email as existing candidate
        request.setEmail("rahul@gmail.com");

        CandidateResponse response = candidateService.updateCandidate(1L, request);

        assertThat(response.getRole()).isEqualTo("Backend Developer");
        assertThat(response.getStatus()).isEqualTo(CandidateStatus.SHORTLISTED);
        verify(candidateRepository, never()).existsByEmailIgnoreCase(anyString());
    }

    @Test
    void updateCandidate_throwsDuplicateEmailException_whenNewEmailBelongsToAnotherCandidate() {
        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateRepository.existsByEmailIgnoreCase("other@gmail.com")).thenReturn(true);

        request.setEmail("other@gmail.com");

        assertThatThrownBy(() -> candidateService.updateCandidate(1L, request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(candidateRepository, never()).save(any(Candidate.class));
    }

    @Test
    void updateCandidate_throwsNotFoundException_whenCandidateMissing() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateService.updateCandidate(99L, request))
                .isInstanceOf(CandidateNotFoundException.class);
    }

    @Test
    void searchCandidates_filtersByRoleAndStatus() {
        when(candidateRepository.findByRoleIgnoreCaseAndStatus("Java Developer", CandidateStatus.APPLIED))
                .thenReturn(List.of(candidate));

        List<CandidateResponse> results = candidateService.searchCandidates("Java Developer", CandidateStatus.APPLIED);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getRole()).isEqualTo("Java Developer");
    }

    @Test
    void searchCandidates_filtersByRoleOnly() {
        when(candidateRepository.findByRoleIgnoreCase("Java Developer")).thenReturn(List.of(candidate));

        List<CandidateResponse> results = candidateService.searchCandidates("Java Developer", null);

        assertThat(results).hasSize(1);
    }

    @Test
    void searchCandidates_filtersByStatusOnly() {
        when(candidateRepository.findByStatus(CandidateStatus.APPLIED)).thenReturn(List.of(candidate));

        List<CandidateResponse> results = candidateService.searchCandidates(null, CandidateStatus.APPLIED);

        assertThat(results).hasSize(1);
    }
}
