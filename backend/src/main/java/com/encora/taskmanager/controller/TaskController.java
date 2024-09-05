package com.encora.taskmanager.controller;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
        try {
            Pageable pageable = Pageable.ofSize(size).withPage(page);
            Page<Task> tasks;

            if (status != null && !status.isEmpty()) {
                List<Task.Status> statuses = convertToListStatus(status);
                tasks = taskService.getTasksByStatusIn(statuses, pageable);
            } else if (dueDateAfter != null || dueDateBefore != null) {
                tasks = taskService.getTasksByDueDateRange(dueDateAfter, dueDateBefore, pageable);
            } else if (sort != null) {
                Sort.Direction sortDirection = Sort.Direction.fromString(sort.toUpperCase());
                tasks = taskService.getTasksSortedByDueDate(pageable, sortDirection);
            } else {
                tasks = taskService.getAllTasks(pageable);
            }

            GenericResponse<Page<Task>> response = new GenericResponse<>(
                    GenericResponse.Status.SUCCESS,
                    "Tasks retrieved successfully",
                    tasks
            );
            return ResponseEntity.ok(response);
        } catch (TaskManagerException e) {
            GenericResponse<Page<Task>> response = new GenericResponse<>(
                    GenericResponse.Status.FAILED,
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private List<Task.Status> convertToListStatus(String status) {
        // Split the comma-separated status string into a list of Task.Status enums
        return Arrays.stream(status.split(","))
                .map(s -> Task.Status.valueOf(s.trim().toUpperCase()))
                .collect(Collectors.toList());
    }
}
