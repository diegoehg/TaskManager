package com.encora.taskmanager.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record Task(
        @NotNull(groups = PutTaskInfo.class)
        Long id,
        String description,
        LocalDate dueDate,
        Status status) {
    public enum Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }
}
