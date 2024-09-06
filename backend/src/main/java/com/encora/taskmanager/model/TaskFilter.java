package com.encora.taskmanager.model;

import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

public record TaskFilter(List<Task.Status> statuses, LocalDate dueDateAfter, LocalDate dueDateBefore, Sort.Direction sortDirection) {}