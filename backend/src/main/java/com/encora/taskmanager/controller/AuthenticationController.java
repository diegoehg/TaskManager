package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.AuthenticationCredentialsRequest;
import com.encora.taskmanager.model.GenericResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

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