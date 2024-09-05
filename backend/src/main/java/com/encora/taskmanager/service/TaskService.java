package com.encora.taskmanager.service;

import com.encora.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TaskService {
    Page<Task> getAllTasks(Pageable pageable);

    Page<Task> getTasksByStatusIn(List<Task.Status> statuses, Pageable pageable);

    Page<Task> getTasksByDueDateRange(LocalDate dueDateAfter, LocalDate dueDateBefore, Pageable pageable);
}
