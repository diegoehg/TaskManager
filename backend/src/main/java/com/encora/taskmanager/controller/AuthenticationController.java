package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.*;
import com.encora.taskmanager.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final Map<String, Integer> invalidLoginAttempts = new HashMap<>();
    public static final int MAX_INVALID_ATTEMPTS = 3;
    private static final long JWT_EXPIRATION_TIME = 3600;

    @Autowired
    private UserAccountService userAccountService;

    @PostMapping("/signup")
    public ResponseEntity<GenericResponse<Void>> signup(@Valid @RequestBody AuthenticationCredentialsRequest request) {
        userAccountService.registerUser(new UserAccount(null, request.username(), request.password(), null, 0, null));

        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.SUCCESS,
                "User registered successfully!",
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<LoginResponse>> login(@Valid @RequestBody AuthenticationCredentialsRequest request)
            throws CredentialNotFoundException, FailedLoginException {
        String username = request.username();
        String password = request.password();

        User user = userAccountService.validateUserAccount(request.username(), request.password());
        if (user != null) {
            // TODO Implement session management - login
            // Successful authentication
            String accessToken = generateJwtToken(user); // Implement JWT token generation
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

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request) {
        String username = extractUsernameFromJwt(request); // Implement JWT extraction logic
        if (username != null) {
            // TODO Implement session management - logout
            invalidLoginAttempts.remove(username);
            GenericResponse<Void> response = new GenericResponse<>(GenericResponse.Status.SUCCESS, "Logout successful", null);
            return ResponseEntity.ok(response);
        } else {
            GenericResponse<Void> response = new GenericResponse<>(GenericResponse.Status.FAILED, "Invalid or missing JWT token", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // TODO: Implement these methods for JWT and refresh token generation
    private String generateJwtToken(User user) {
        return "generated-jwt-token";
    }

    private String generateRefreshToken() {
        return "generated-refresh-token";
    }

    // TODO: Implement this method for JWT extraction
    private String extractUsernameFromJwt(HttpServletRequest request) {
        // Logic to extract username from JWT token in the request header
        // For example, using Spring Security:
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
        //     UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        //     return userDetails.getUsername();
        // }
        String jwt = request.getHeader("Authorization").substring(7);
        if (jwt.endsWith("user@example.com"))
            return jwt.substring(16);
        else
            return null;
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