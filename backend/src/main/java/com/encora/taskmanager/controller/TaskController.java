package com.encora.taskmanager.controller;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/tasks")
    public ResponseEntity<GenericResponse<Page<Task>>> getAllTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            Page<Task> tasks = taskService.getAllTasks(Pageable.ofSize(size).withPage(page));
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
}
