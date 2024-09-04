package com.encora.taskmanager.controller;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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
        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(1L, "Task 1", LocalDate.of(2024, 5, 7), Task.Status.PENDING),
                        new Task(2L, "Task 2", LocalDate.of(2024, 11, 24), Task.Status.IN_PROGRESS)
                ),
                Pageable.ofSize(2).withPage(0),
                10 // Total elements (assume 10 for this example)
        );
        when(taskService.getAllTasks(Pageable.ofSize(10).withPage(0))).thenReturn(taskPage);

        // Perform GET request
        mockMvc.perform(get("/api/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name())) // Verify status
                .andExpect(jsonPath("$.message").value("Tasks retrieved successfully")) // Verify message
                .andExpect(jsonPath("$.content.content", hasSize(2))); // Verify 2 tasks are returned in content
    }

    @Test
    public void shouldReturn500WhenServiceThrowsTaskManagerException() throws Exception {
        when(taskService.getAllTasks(Pageable.ofSize(10).withPage(0))).thenThrow(new TaskManagerException("Error retrieving tasks"));

        mockMvc.perform(get("/api/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()) // Expect a 500 error
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.FAILED.name()))
                .andExpect(jsonPath("$.message").value("Error retrieving tasks"));
    }

    @Test
    public void shouldReturnPaginatedListOfTasks() throws Exception {
        // Mock data for page 0, size 2
        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(1L, "Task 1", LocalDate.of(2024, 5, 7), Task.Status.COMPLETED),
                        new Task(2L, "Task 2", LocalDate.of(2024, 11, 24), Task.Status.IN_PROGRESS)
                ),
                Pageable.ofSize(2).withPage(0),
                10 // Total elements (assume 10 for this example)
        );
        when(taskService.getAllTasks(Pageable.ofSize(2).withPage(0))).thenReturn(taskPage);

        // Perform GET request with pagination parameters
        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.message").value("Tasks retrieved successfully"))
                .andExpect(jsonPath("$.content.content", hasSize(2))); // Verify 2 tasks in the page content
    }
}