package com.encora.taskmanager.service;

import com.encora.taskmanager.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Page<Task> getAllTasks(Pageable pageable);
}
