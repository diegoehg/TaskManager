package com.encora.taskmanager.controller;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.GenericResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import com.encora.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

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
        TaskFilter taskFilter = new TaskFilter(null, null, null, null);
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(10).withPage(0)))
                .thenReturn(taskPage);

        // Perform GET request
        mockMvc.perform(get("/api/tasks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name())) // Verify status
                .andExpect(jsonPath("$.message").value("Tasks retrieved successfully")) // Verify message
                .andExpect(jsonPath("$.data.content", hasSize(2))); // Verify 2 tasks are returned in content
    }

    @Test
    public void shouldReturn500WhenServiceThrowsTaskManagerException() throws Exception {
        when(taskService.getAllTasks(any(TaskFilter.class), any(Pageable.class)))
                .thenThrow(new TaskManagerException("Error retrieving tasks"));

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
        TaskFilter taskFilter = new TaskFilter(null, null, null, null);
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(2).withPage(0)))
                .thenReturn(taskPage);

        // Perform GET request with pagination parameters
        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.message").value("Tasks retrieved successfully"))
                .andExpect(jsonPath("$.data.content", hasSize(2))); // Verify 2 tasks in the page content
    }

    @Test
    public void shouldReturnFilteredListOfTasks() throws Exception {
        // Mock data for PENDING and IN_PROGRESS tasks
        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(1L, "Task 1", LocalDate.of(2024, 5, 7), Task.Status.PENDING),
                        new Task(3L, "Task 3", LocalDate.of(2024, 12, 31), Task.Status.IN_PROGRESS)
                ),
                Pageable.ofSize(10).withPage(0),
                2 // Total elements
        );
        TaskFilter taskFilter = new TaskFilter(List.of(Task.Status.PENDING, Task.Status.IN_PROGRESS), null, null, null);
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(10).withPage(0)))
                .thenReturn(taskPage);

        // Perform GET request with filtering by status
        mockMvc.perform(get("/api/tasks")
                        .param("status", "PENDING,IN_PROGRESS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.message").value("Tasks retrieved successfully"))
                .andExpect(jsonPath("$.data.content", hasSize(2))); // Verify 2 tasks are returned
    }

    @Test
    public void shouldReturnTasksFilteredByDueDateAfter() throws Exception {
        LocalDate dueDateAfter = LocalDate.parse("2024-09-14");
        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(2L, "Task 2", LocalDate.of(2024, 10, 15), Task.Status.IN_PROGRESS),
                        new Task(3L, "Task 3", LocalDate.of(2024, 12, 31), Task.Status.PENDING)
                ),
                Pageable.ofSize(10).withPage(0),
                2
        );
        TaskFilter taskFilter = new TaskFilter(null, dueDateAfter, null, null);
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(10).withPage(0)))
                .thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                        .param("dueDateAfter", "2024-09-14")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    public void shouldReturnTasksFilteredByDueDateBefore() throws Exception {
        LocalDate dueDateBefore = LocalDate.parse("2024-12-25");
        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(1L, "Task 1", LocalDate.of(2024, 5, 7), Task.Status.COMPLETED),
                        new Task(2L, "Task 2", LocalDate.of(2024, 10, 15), Task.Status.IN_PROGRESS)
                ),
                Pageable.ofSize(10).withPage(0),
                2
        );
        TaskFilter taskFilter = new TaskFilter(null, null, dueDateBefore, null);
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(10).withPage(0)))
                .thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                        .param("dueDateBefore", "2024-12-25")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    public void shouldReturnTasksFilteredByDueDateBetween() throws Exception {
        LocalDate dueDateAfter = LocalDate.parse("2024-09-25");
        LocalDate dueDateBefore = LocalDate.parse("2025-04-05");
        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(2L, "Task 2", LocalDate.of(2024, 10, 15), Task.Status.IN_PROGRESS),
                        new Task(3L, "Task 3", LocalDate.of(2024, 12, 31), Task.Status.PENDING)
                ),
                Pageable.ofSize(10).withPage(0),
                2
        );
        TaskFilter taskFilter = new TaskFilter(null, dueDateAfter, dueDateBefore, null);
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(10).withPage(0)))
                .thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                        .param("dueDateAfter", "2024-09-25")
                        .param("dueDateBefore", "2025-04-05")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.data.content", hasSize(2)));
    }

    @Test
    public void shouldReturnTasksSortedByDueDateAscending() throws Exception {
        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(1L, "Task 1", LocalDate.of(2024, 5, 7), Task.Status.COMPLETED),
                        new Task(2L, "Task 2", LocalDate.of(2024, 10, 15), Task.Status.IN_PROGRESS),
                        new Task(3L, "Task 3", LocalDate.of(2024, 12, 31), Task.Status.PENDING)
                ),
                Pageable.ofSize(10).withPage(0),
                3
        );
        TaskFilter taskFilter = new TaskFilter(null, null, null, Sort.Direction.ASC);
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(10).withPage(0)))
                .thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                        .param("sort", "ASC")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.data.content", hasSize(3)));
    }

    @Test
    public void shouldReturnTasksSortedByDueDateDescending() throws Exception {
        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(3L, "Task 3", LocalDate.of(2024, 12, 31), Task.Status.PENDING),
                        new Task(2L, "Task 2", LocalDate.of(2024, 10, 15), Task.Status.IN_PROGRESS),
                        new Task(1L, "Task 1", LocalDate.of(2024, 5, 7), Task.Status.COMPLETED)
                ),
                Pageable.ofSize(10).withPage(0),
                3
        );
        TaskFilter taskFilter = new TaskFilter(null, null, null, Sort.Direction.DESC);
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(10).withPage(0)))
                .thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                        .param("sort", "DESC")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GenericResponse.Status.SUCCESS.name()))
                .andExpect(jsonPath("$.data.content", hasSize(3)));
    }

    @Test
    public void shouldReturnTasksFilteredByAllParameters() throws Exception {
        LocalDate dueDateAfter = LocalDate.parse("2024-09-14");
        LocalDate dueDateBefore = LocalDate.parse("2024-12-25");

        Page<Task> taskPage = new PageImpl<>(
                List.of(
                        new Task(2L, "Task 2", LocalDate.of(2024, 10, 15), Task.Status.IN_PROGRESS)
                ),
                Pageable.ofSize(10).withPage(0),
                1
        );

        TaskFilter taskFilter = new TaskFilter(
                List.of(Task.Status.IN_PROGRESS),
                dueDateAfter,
                dueDateBefore,
                Sort.Direction.ASC
        );
        when(taskService.getAllTasks(taskFilter, Pageable.ofSize(10).withPage(0)))
                .thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "IN_PROGRESS")
                        .param("dueDateAfter", "2024-09-14")
                        .param("dueDateBefore", "2024-12-25")
                        .param("sort", "ASC")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    public void shouldReturnTaskById() throws Exception {
        long taskId = 1L;
        Task task = new Task(taskId, "Task 1", LocalDate.of(2024, 5, 7), Task.Status.COMPLETED);
        when(taskService.getTaskById(taskId)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(taskId))
                .andExpect(jsonPath("$.data.description").value("Task 1"));
    }

    @Test
    public void shouldReturnNotFoundWhenTaskNotFound() throws Exception {
        long taskId = 999L;
        when(taskService.getTaskById(taskId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + taskId));
    }

    @Test
    public void shouldCreateTask() throws Exception {
        Task taskToCreate = new Task(null, "New Task", LocalDate.of(2024, 12, 31), Task.Status.PENDING);
        Task createdTask = new Task(1L, "New Task", LocalDate.of(2024, 12, 31), Task.Status.PENDING);

        when(taskService.createTask(taskToCreate)).thenReturn(createdTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskToCreate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.description").value("New Task"));
    }

    @Test
    public void shouldReturnBadRequestForPostMalformedRequestBody() throws Exception {
        String invalidJson = """
                        { 
                            "description": "Invalid Task", 
                            "dueDate": "invalid-date" 
                        }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Malformed task request body."));
    }

    @Test
    public void shouldUpdateTask() throws Exception {
        Task existingTask = new Task(1L, "Existing Task", LocalDate.of(2024, 12, 25), Task.Status.PENDING);
        Task updatedTask = new Task(1L, "Updated Task", LocalDate.of(2025, 1, 1), Task.Status.IN_PROGRESS);

        when(taskService.getTaskById(1L)).thenReturn(Optional.of(existingTask));
        when(taskService.updateTask(updatedTask)).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.description").value("Updated Task"))
                .andExpect(jsonPath("$.data.dueDate").value("2025-01-01"))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    public void shouldReturnNotFoundWhenUpdatingNonExistingTask() throws Exception {
        Task updatedTask = new Task(999L, "Updated Task", LocalDate.of(2025, 1, 1), Task.Status.IN_PROGRESS);

        when(taskService.getTaskById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Task not found with ID: 999"));
    }

    @Test
    public void shouldReturnBadRequestForMalformedPutRequestBody() throws Exception {
        String invalidJson = """
                        { 
                            "description": "Invalid Task",
                            "status": 34534534534
                        }
                """;

        mockMvc.perform(put("/api/tasks/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.message").value("Malformed task request body."));
    }
}