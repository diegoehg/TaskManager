package com.encora.taskmanager.service;

import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TaskService {
    Page<Task> getAllTasks(TaskFilter taskFilter, Pageable pageable);

    Optional<Task> getTaskById(Long id);

    Task createTask(Task task);

    Task updateTask(Task task);
}
