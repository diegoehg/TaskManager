package com.encora.taskmanager.controller;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.PagedResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import com.encora.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping(value = "/tasks")
    public ResponseEntity<GenericResponse<PagedResponse<Task>>> getAllTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "dueDateAfter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateAfter,
            @RequestParam(value = "dueDateBefore", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateBefore,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        List<Task.Status> statuses = status != null ? convertToListStatus(status) : null;
        TaskFilter.SortDirection sortDirection = sort != null ? TaskFilter.SortDirection.valueOf(sort.toUpperCase()) : null;

        TaskFilter taskFilter = new TaskFilter(
                statuses,
                dueDateAfter,
                dueDateBefore,
                sortDirection
        );

        PagedResponse<Task> tasks = taskService.getAllTasks(taskFilter, page, size);

        GenericResponse<PagedResponse<Task>> response = new GenericResponse<>(
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
    public ResponseEntity<GenericResponse<Task>> getTaskById(@PathVariable String id) {
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            GenericResponse<Task> response = new GenericResponse<>(
                    GenericResponse.Status.SUCCESS,
                    "Task retrieved successfully",
                    task.get()
            );
            return ResponseEntity.ok(response);
        } else {
            return handleTaskNotFound(id, Task.class);
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<GenericResponse<Task>> createTask(@Valid @RequestBody Task task) throws URISyntaxException {
        Task createdTask = taskService.createTask(task);
        GenericResponse<Task> response = new GenericResponse<>(
                GenericResponse.Status.SUCCESS,
                "Task created successfully",
                createdTask
        );
        return ResponseEntity.created(new URI("api/tasks/" + createdTask.id())).body(response);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<GenericResponse<Task>> updateTask(@PathVariable String id, @Valid @RequestBody Task task) {
        Optional<Task> existingTask = taskService.getTaskById(task.id());
        if (existingTask.isPresent()) {
            Task updatedTask = taskService.updateTask(task);
            GenericResponse<Task> response = new GenericResponse<>(
                    GenericResponse.Status.SUCCESS,
                    "Task updated successfully",
                    updatedTask
            );
            return ResponseEntity.ok(response);
        } else {
            return handleTaskNotFound(id, Task.class);
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<GenericResponse<Void>> deleteTask(@PathVariable String id) {
        Optional<Task> existingTask = taskService.getTaskById(id);
        if (existingTask.isPresent()) {
            taskService.deleteTask(id);
            GenericResponse<Void> response = new GenericResponse<>(
                    GenericResponse.Status.SUCCESS,
                    "Task deleted successfully",
                    null
            );
            return ResponseEntity.ok(response);
        } else {
            return handleTaskNotFound(id, Void.class);
        }
    }

    private <T> ResponseEntity<GenericResponse<T>> handleTaskNotFound(String id, Class<T> type) {
        GenericResponse<T> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                "Task not found with ID: " + id,
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(TaskManagerException.class)
    public ResponseEntity<GenericResponse<Void>> handleTaskManagerExceptions(TaskManagerException ex) {
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                ex.getMessage(),
                null
        );
        return ResponseEntity.internalServerError().body(response);
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

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<GenericResponse<Void>> handleURISyntaxException(URISyntaxException ex) {
        GenericResponse<Void> response = new GenericResponse<>(
                GenericResponse.Status.FAILED,
                "Invalid task ID.",
                null
        );
        return ResponseEntity.internalServerError().body(response);
    }
}
