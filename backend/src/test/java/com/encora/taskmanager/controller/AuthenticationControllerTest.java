package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.AuthenticationCredentialsRequest;
import com.encora.taskmanager.model.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

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

    @ParameterizedTest
    @ValueSource(strings = {"Sh0rt$", "n0_upperc4s3", "N0%L0W3RC4S3", "No_Number_Password", "N0Sp3cialCh4racter"})
    public void testSignup_InvalidPassword_ThrowsException(String invalidPassword) throws Exception {
        AuthenticationCredentialsRequest request = new AuthenticationCredentialsRequest("test@example.com", invalidPassword);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.FAILED.name()));
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

    @Test
    public void testLogin_ValidCredentials_ReturnsOk() throws Exception {
        AuthenticationCredentialsRequest request = new AuthenticationCredentialsRequest("user@example.com", "Password123!");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.message").value("Authentication successful"))
                .andExpect(jsonPath("$.data.access_token").value("generated-jwt-token"))
                .andExpect(jsonPath("$.data.token_type").value("Bearer"))
                .andExpect(jsonPath("$.data.expires_in").value(3600))
                .andExpect(jsonPath("data.refresh_token").value("generated-refresh-token"));
    }

    @ParameterizedTest
    @MethodSource("parametersFor_testLogin_InvalidCredentials_ReturnsUnauthorized")
    public void testLogin_InvalidCredentials_ReturnsUnauthorized(AuthenticationCredentialsRequest invalidCredential) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCredential)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.FAILED.name()))
                .andExpect(jsonPath("$.message").value("Authentication not authorized"));
    }

    private static Stream<Arguments> parametersFor_testLogin_InvalidCredentials_ReturnsUnauthorized() {
        return Stream.of(
                Arguments.of(new AuthenticationCredentialsRequest("invalid@email.com", "Password123!")),
                Arguments.of(new AuthenticationCredentialsRequest("user@example.com", "wrongPassword123!"))

        );
    }

    @Test
    public void testLogin_TooManyInvalidAttempts_ReturnsUnauthorized() throws Exception {
        AuthenticationCredentialsRequest request = new AuthenticationCredentialsRequest("user@example.com", "wrongPassword123!");

        // Perform multiple invalid login attempts
        for (int i = 0; i < AuthenticationController.MAX_INVALID_ATTEMPTS; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.status").value(GenericResponse.Status.FAILED.name()));
        }

        // Attempt to log in again (should be blocked)
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.FAILED.name()))
                .andExpect(jsonPath("$.message").value("Authentication not authorized"));
    }

    @Test
    public void testLogout_ValidJwt_ReturnsOk() throws Exception {
        String username = "user@example.com";
        String jwtToken = "valid-jwt-token " + username; // Replace with actual token generation

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    public void testLogout_InvalidJwt_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid-jwt-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.FAILED.name()))
                .andExpect(jsonPath("$.message").value("Invalid or missing JWT token"));
    }
}