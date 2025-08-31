package com.encora.taskmanager.service;

import com.encora.taskmanager.model.PagedResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;

import java.util.Optional;

public interface TaskService {
    PagedResponse<Task> getAllTasks(TaskFilter taskFilter, int page, int size);

    Optional<Task> getTaskById(String id);

    Task createTask(Task task);

    Task updateTask(Task task);

    void deleteTask(String id);
}
