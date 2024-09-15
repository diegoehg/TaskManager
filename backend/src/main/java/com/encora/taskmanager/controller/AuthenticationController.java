package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.AuthenticationCredentialsRequest;
import com.encora.taskmanager.model.GenericResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    @PostMapping("/signup")
    public ResponseEntity<GenericResponse<Void>> signup(@Valid @RequestBody AuthenticationCredentialsRequest request) {
        if (!isValidPassword(request.password())) {
            GenericResponse<Void> response = new GenericResponse<>(
                    GenericResponse.Status.FAILED,
                    "Invalid password. It must meet the following criteria: At least 8 characters, one " +
                            "uppercase letter, one lowercase letter, one number, and one special character.",
                    null
            );
            return ResponseEntity.badRequest().body(response);
        }

        // TODO: Implement user registration logic (e.g., save user to database)
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.SUCCESS,
                "User registered successfully!",
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
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