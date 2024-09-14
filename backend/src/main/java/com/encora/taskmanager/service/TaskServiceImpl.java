package com.encora.taskmanager.service;

import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import com.encora.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Page<Task> getAllTasks(TaskFilter taskFilter, Pageable pageable) {
        // Add logic for filtering, sorting, and pagination based on taskFilter
        // For simplicity, returning all tasks for now
        List<Task> tasks = taskRepository.findAll().stream()
                .filter(task -> taskFilter.statuses() == null || taskFilter.statuses().isEmpty() || taskFilter.statuses().contains(task.status()))
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return new PageImpl<>(tasks, pageable, tasks.size());
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}