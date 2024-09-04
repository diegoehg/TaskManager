package com.encora.taskmanager.model;

import java.time.LocalDate;

public record Task(long id, String description, LocalDate dueDate, boolean completed) {
}
