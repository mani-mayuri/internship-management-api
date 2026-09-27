package com.example.internshipmanagement.controller;

import com.example.internshipmanagement.dto.CandidateRequest;
import com.example.internshipmanagement.entity.CandidateStatus;
import com.example.internshipmanagement.repository.CandidateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end style tests that go through the full Spring stack: controller,
 * service, repository and an in-memory H2 database, using MockMvc instead
 * of a real HTTP client.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CandidateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CandidateRepository candidateRepository;

    @BeforeEach
    void cleanDatabase() {
        candidateRepository.deleteAll();
    }

    private CandidateRequest sampleRequest() {
        CandidateRequest request = new CandidateRequest();
        request.setName("Rahul Kumar");
        request.setEmail("rahul@gmail.com");
        request.setPhone("9876543210");
        request.setRole("Java Developer");
        request.setStatus(CandidateStatus.APPLIED);
        return request;
    }

    // ---------- Authentication ----------

    @Test
    void getAllCandidates_withoutCredentials_returns401() throws Exception {
        mockMvc.perform(get("/api/candidates"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllCandidates_withWrongCredentials_returns401() throws Exception {
        mockMvc.perform(get("/api/candidates").with(httpBasic("admin", "wrongpassword")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllCandidates_withCorrectCredentials_returns200() throws Exception {
        mockMvc.perform(get("/api/candidates").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk());
    }

    // ---------- CRUD ----------

    @Test
    @WithMockUser
    void createCandidate_returns201_andBody() throws Exception {
        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("rahul@gmail.com"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser
    void getCandidateById_returns200_whenExists() throws Exception {
        String body = mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(get("/api/candidates/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rahul Kumar"));
    }

    @Test
    @WithMockUser
    void getCandidateById_returns404_whenMissing() throws Exception {
        mockMvc.perform(get("/api/candidates/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void updateCandidate_returns200_andUpdatesFields() throws Exception {
        String body = mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        CandidateRequest update = sampleRequest();
        update.setRole("Backend Developer");
        update.setStatus(CandidateStatus.SHORTLISTED);

        mockMvc.perform(put("/api/candidates/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Backend Developer"))
                .andExpect(jsonPath("$.status").value("SHORTLISTED"));
    }

    @Test
    @WithMockUser
    void deleteCandidate_returns204_thenGetReturns404() throws Exception {
        String body = mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/candidates/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/candidates/" + id))
                .andExpect(status().isNotFound());
    }

    // ---------- Duplicate email ----------

    @Test
    @WithMockUser
    void createCandidate_returns409_whenEmailAlreadyExists() throws Exception {
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())));

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Duplicate Email"));
    }

    // ---------- Validation ----------

    @Test
    @WithMockUser
    void createCandidate_returns400_whenNameBlank() throws Exception {
        CandidateRequest request = sampleRequest();
        request.setName("");

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createCandidate_returns400_whenEmailInvalid() throws Exception {
        CandidateRequest request = sampleRequest();
        request.setEmail("not-an-email");

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createCandidate_returns400_whenPhoneInvalid() throws Exception {
        CandidateRequest request = sampleRequest();
        request.setPhone("abc123");

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createCandidate_returns400_whenRoleBlank() throws Exception {
        CandidateRequest request = sampleRequest();
        request.setRole("  ");

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createCandidate_returns400_whenStatusMissing() throws Exception {
        String json = """
                {
                  "name": "Rahul Kumar",
                  "email": "rahul@gmail.com",
                  "phone": "9876543210",
                  "role": "Java Developer"
                }
                """;

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createCandidate_returns400_whenStatusInvalid() throws Exception {
        String json = """
                {
                  "name": "Rahul Kumar",
                  "email": "rahul@gmail.com",
                  "phone": "9876543210",
                  "role": "Java Developer",
                  "status": "NOT_A_REAL_STATUS"
                }
                """;

        mockMvc.perform(post("/api/candidates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ---------- Search ----------

    @Test
    @WithMockUser
    void searchCandidates_byRole_returnsMatches() throws Exception {
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())));

        mockMvc.perform(get("/api/candidates/search").param("role", "java developer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("Java Developer"));
    }

    @Test
    @WithMockUser
    void searchCandidates_byStatus_returnsMatches() throws Exception {
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())));

        mockMvc.perform(get("/api/candidates/search").param("status", "APPLIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPLIED"));
    }

    @Test
    @WithMockUser
    void searchCandidates_byRoleAndStatus_returnsMatches() throws Exception {
        mockMvc.perform(post("/api/candidates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())));

        mockMvc.perform(get("/api/candidates/search")
                        .param("role", "Java Developer")
                        .param("status", "APPLIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("rahul@gmail.com"));
    }

    @Test
    @WithMockUser
    void searchEndpoint_doesNotConflictWithGetById() throws Exception {
        // /api/candidates/search must route to the search handler, not be
        // interpreted as a numeric {id} path variable.
        mockMvc.perform(get("/api/candidates/search"))
                .andExpect(status().isOk());
    }
}
