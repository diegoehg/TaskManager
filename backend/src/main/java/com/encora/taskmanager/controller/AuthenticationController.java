package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.AuthenticationCredentialsRequest;
import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final Map<String, Integer> invalidLoginAttempts = new HashMap<>();
    public static final int MAX_INVALID_ATTEMPTS = 3;
    private static final long JWT_EXPIRATION_TIME = 3600;

    @PostMapping("/signup")
    public ResponseEntity<GenericResponse<Void>> signup(@Valid @RequestBody AuthenticationCredentialsRequest request) {
        // TODO: Implement user registration logic (e.g., save user to database)
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.SUCCESS,
                "User registered successfully!",
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<LoginResponse>> login(@Valid @RequestBody AuthenticationCredentialsRequest request) {
        String username = request.username();
        String password = request.password();

        // TODO: Replace with actual user authentication logic against a database
        if (username.equals("user@example.com") && password.equals("Password123!")) {
            // Successful authentication
            String accessToken = generateJwtToken(username); // Implement JWT token generation
            String refreshToken = generateRefreshToken(); // Implement refresh token generation

            LoginResponse loginResponse = new LoginResponse(accessToken, "Bearer", JWT_EXPIRATION_TIME, refreshToken);
            GenericResponse<LoginResponse> response = new GenericResponse<>(GenericResponse.Status.SUCCESS, "Authentication successful", loginResponse);
            return ResponseEntity.ok(response);
        } else {
            // Authentication failed
            handleInvalidLoginAttempt(username);
            GenericResponse<LoginResponse> response = new GenericResponse<>(GenericResponse.Status.FAILED, "Authentication not authorized", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    private void handleInvalidLoginAttempt(String username) {
        invalidLoginAttempts.put(username, invalidLoginAttempts.getOrDefault(username, 0) + 1);
        if (invalidLoginAttempts.get(username) >= MAX_INVALID_ATTEMPTS) {
            // TODO: Implement logic to block user or take other security measures
            System.out.println("User " + username + " blocked after too many invalid attempts.");
        }
    }

    // TODO: Implement these methods for JWT and refresh token generation
    private String generateJwtToken(String username) {
        return "generated-jwt-token";
    }

    private String generateRefreshToken() {
        return "generated-refresh-token";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                null
        );
        return ResponseEntity.badRequest().body(response);
    }
}