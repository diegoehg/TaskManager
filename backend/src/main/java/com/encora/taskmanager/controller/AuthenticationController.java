package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.*;
import com.encora.taskmanager.service.UserAccountService;
import com.encora.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.CredentialNotFoundException;
import javax.security.auth.login.FailedLoginException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final long JWT_EXPIRATION_TIME = 3600;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private JwtUtil jwtUtil;

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
    public ResponseEntity<GenericResponse<LoginResponse>> login(@RequestBody AuthenticationCredentialsRequest request)
            throws CredentialNotFoundException, FailedLoginException, AccountLockedException {
        User user = userAccountService.validateUserAccount(request.username(), request.password());

        String accessToken = jwtUtil.generateToken(user.username());
        String refreshToken = generateRefreshToken();

        LoginResponse loginResponse = new LoginResponse(accessToken, "Bearer", JWT_EXPIRATION_TIME, refreshToken);
        GenericResponse<LoginResponse> response = new GenericResponse<>(GenericResponse.Status.SUCCESS, "Authentication successful", loginResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request) {
        String username = extractUsernameFromJwt(request); // Implement JWT extraction logic
        if (username != null) {
            // TODO Implement session management - logout
            GenericResponse<Void> response = new GenericResponse<>(GenericResponse.Status.SUCCESS, "Logout successful", null);
            return ResponseEntity.ok(response);
        } else {
            GenericResponse<Void> response = new GenericResponse<>(GenericResponse.Status.FAILED, "Invalid or missing JWT token", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // TODO: Implement this methods for refresh token generation
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

    @ExceptionHandler(CredentialNotFoundException.class)
    public ResponseEntity<GenericResponse<Void>> handleCredentialNotFoundException(CredentialNotFoundException ex) {
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<GenericResponse<Void>> handleAccountLockedException(AccountLockedException ex) {
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(FailedLoginException.class)
    public ResponseEntity<GenericResponse<Void>> handleFailedLoginException(FailedLoginException ex) {
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}