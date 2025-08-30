package com.encora.taskmanager.model;

import java.time.LocalDate;
import java.util.List;

public record TaskFilter(List<Task.Status> statuses, LocalDate dueDateAfter, LocalDate dueDateBefore, SortDirection sortDirection) {
    public enum SortDirection {
        ASC,
        DESC
    }
}