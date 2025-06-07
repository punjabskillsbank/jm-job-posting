package com.jobmatrix.exceptionHandling;

import com.common.exceptionHandling.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    private static class TestController {
        @GetMapping("/test-category-not-found")
        public String throwCategoryNotFoundException() {
            throw new CategoryNotFoundException(5L); // Correct usage
        }

        @GetMapping("/test-job-posting-not-found")
        public String throwJobPostingNotFoundException() {
            throw new JobPostingNotFoundException(7L);
        }

        @GetMapping("/test-client-not-found")
        public String throwClientNotFoundException() {
            throw new ClientNotFoundException(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
        }

        @GetMapping("/test-question-limit-exceeded")
        public String throwQuestionLimitExceedException() {
            throw new QuestionLimitExceedException();
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleCategoryNotFound_ShouldReturnNotFoundWithMessage() throws Exception {
        mockMvc.perform(get("/test-category-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found at given categoryId: 5"));
    }

    @Test
    void handleJobPostingNotFound_ShouldReturnNotFoundWithMessage() throws Exception {
        mockMvc.perform(get("/test-job-posting-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("JobPosting not found at given jobPostingId: 7"));
    }

    @Test
    void handleClientNotFound_ShouldReturnNotFoundWithMessage() throws Exception {
        mockMvc.perform(get("/test-client-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Client not found with ID: 123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    void handleQuestionLimitExceeded_ShouldReturnBadRequestWithMessage() throws Exception {
        mockMvc.perform(get("/test-question-limit-exceeded")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Question Limit Exceeded. Maximum 5 questions are allowed."));
    }
}
