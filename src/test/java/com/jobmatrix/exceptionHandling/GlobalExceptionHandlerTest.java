package com.jobmatrix.exceptionHandling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

        @GetMapping("/test-job-posting-not-found-custom")
        public String throwJobPostingNotFoundExceptionWithCustomMessage() {
            throw new JobPostingNotFoundException("Custom error message: Job posting not found");
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
    void handleJobPostingNotFoundWithCustomMessage_ShouldReturnNotFoundWithMessage() throws Exception {
        mockMvc.perform(get("/test-job-posting-not-found-custom")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Custom error message: Job posting not found"));
    }
    

}
