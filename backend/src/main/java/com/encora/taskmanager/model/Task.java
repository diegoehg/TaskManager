package com.encora.taskmanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "tasks")
public record Task(@Id Long id, String description, LocalDate dueDate, Status status) {
    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }
}
