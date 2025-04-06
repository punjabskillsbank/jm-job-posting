package com.jobmatrix.exceptionHandling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest{

    private MockMvc mockMvc;

    // Test controller that throws our custom exception
    @RestController
    private static class TestController {
        @GetMapping("/test-category-not-found")
        public String throwCategoryNotFoundException() {
            throw new CategoryNotFoundException("Test category not found");
        }
    }

    @BeforeEach
    void setUp() {
        // Set up MockMvc with our test controller and the exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandlerTest())
                .build();
    }

    @Test
    void handleCategoryNotFound_ShouldReturnNotFoundWithCorrectBody() throws Exception {
        // Perform request that will trigger the exception
        mockMvc.perform(get("/test-category-not-found")
                        .contentType(MediaType.APPLICATION_JSON))
                // Verify HTTP status
                .andExpect(status().isNotFound())
                // Verify response body contains the expected fields
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.error", is("Category Not Found")))
                .andExpect(jsonPath("$.message", is("Test category not found")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }
}