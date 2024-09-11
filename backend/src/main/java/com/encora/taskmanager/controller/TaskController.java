package com.encora.taskmanager.controller;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import com.encora.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/tasks")
    public ResponseEntity<GenericResponse<Page<Task>>> getAllTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "dueDateAfter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateAfter,
            @RequestParam(value = "dueDateBefore", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateBefore,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        List<Task.Status> statuses = status != null ? convertToListStatus(status) : null;
        Sort.Direction sortDirection = sort != null ? Sort.Direction.fromString(sort.toUpperCase()) : null;

        TaskFilter taskFilter = new TaskFilter(
                statuses,
                dueDateAfter,
                dueDateBefore,
                sortDirection
        );

        Page<Task> tasks = taskService.getAllTasks(taskFilter, pageable);

        GenericResponse<Page<Task>> response = new GenericResponse<>(
                GenericResponse.Status.SUCCESS,
                "Tasks retrieved successfully",
                tasks
        );
        return ResponseEntity.ok(response);
    }

    private List<Task.Status> convertToListStatus(String status) {
        // Split the comma-separated status string into a list of Task.Status enums
        return Arrays.stream(status.split(","))
                .map(s -> Task.Status.valueOf(s.trim().toUpperCase()))
                .collect(Collectors.toList());
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<GenericResponse<Task>> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            GenericResponse<Task> response = new GenericResponse<>(
                    GenericResponse.Status.SUCCESS,
                    "Task retrieved successfully",
                    task.get()
            );
            return ResponseEntity.ok(response);
        } else {
            GenericResponse<Task> response = new GenericResponse<>(
                    GenericResponse.Status.FAILED,
                    "Task not found with ID: " + id,
                    null
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<GenericResponse<Task>> createTask(@RequestBody Task task) {
        Task createdTask = taskService.createTask(task);
        GenericResponse<Task> response = new GenericResponse<>(
                GenericResponse.Status.SUCCESS,
                "Task created successfully",
                createdTask
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(TaskManagerException.class)
    public ResponseEntity<GenericResponse<Void>> handleTaskManagerExceptions(TaskManagerException ex) {
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<GenericResponse<Void>> handleHttpMessageNotReadableExceptions(HttpMessageNotReadableException ex) {
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                "Malformed task request body.",
                null
        );
        return ResponseEntity.badRequest().body(response);
    }
}
