package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/tasks")
    public ResponseEntity<GenericResponse<List<Task>>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        GenericResponse<List<Task>> response = new GenericResponse<>(
                GenericResponse.Status.SUCCESS,
                "Tasks retrieved successfully",
                tasks
        );
        return ResponseEntity.ok(response);
    }
}
