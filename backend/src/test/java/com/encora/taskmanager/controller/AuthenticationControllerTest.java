package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.AuthenticationCredentialsRequest;
import com.encora.taskmanager.model.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignup_ValidPassword_ReturnsCreated() throws Exception {
        AuthenticationCredentialsRequest request = new AuthenticationCredentialsRequest("test@example.com", "Password123!");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testSignup_InvalidPassword_ThrowsException() throws Exception {
        AuthenticationCredentialsRequest request = new AuthenticationCredentialsRequest("test@example.com", "password");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid.email", "email_456@domain_without_dot"})
    public void testSignup_InvalidEmail_ReturnsBadRequest(String invalidEmail) throws Exception {
        AuthenticationCredentialsRequest request = new AuthenticationCredentialsRequest(invalidEmail, "Password123!");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.FAILED.name()));
    }
}