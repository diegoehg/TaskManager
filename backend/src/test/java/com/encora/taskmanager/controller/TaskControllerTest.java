package com.encora.taskmanager.controller;

import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    public void shouldReturnListOfTasks() throws Exception {
        // Mock data
        List<Task> tasks = List.of(
                new Task(1L, "Task 1", false),
                new Task(2L, "Task 2", true)
        );
        when(taskService.getAllTasks()).thenReturn(tasks);

        // Perform GET request
        mockMvc.perform(get("/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name())) // Verify status
                .andExpect(jsonPath("$.message").value("Tasks retrieved successfully")) // Verify message
                .andExpect(jsonPath("$.content", hasSize(2))); // Verify 2 tasks are returned in content
    }
}