package com.encora.taskmanager.service;

import com.encora.taskmanager.model.PagedResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import com.encora.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTasksWithStatusFilter() {
        // Sample data
        Task task1 = new Task("1", "Task 1", LocalDate.of(2024, 12, 20), Task.Status.PENDING);
        Task task2 = new Task("2", "Task 2", LocalDate.of(2024, 12, 25), Task.Status.IN_PROGRESS);
        List<Task> filteredPageItems = List.of(task1, task2);

        when(mongoTemplate.count(any(Query.class), eq(Task.class))).thenReturn(5L);
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(filteredPageItems);

        // Test filtering by status
        TaskFilter taskFilter = new TaskFilter(List.of(Task.Status.PENDING, Task.Status.IN_PROGRESS), null, null, null);
        PagedResponse<Task> result = taskService.getAllTasks(taskFilter, 0, 2);

        assertEquals(5, result.getTotalElements());
        assertEquals(List.of(task1, task2), result.getItems());
    }

    @Test
    public void testGetAllTasksWithDueDateFilter() {
        // Sample data (this list represents what the DB would return for page 1, size 2)
        Task task5 = new Task("5", "Task 5", LocalDate.of(2024, 12, 18), Task.Status.IN_PROGRESS);
        Task task6 = new Task("6", "Task 6", LocalDate.of(2024, 12, 29), Task.Status.COMPLETED);
        List<Task> filteredPageItems = List.of(task5, task6);

        when(mongoTemplate.count(any(Query.class), eq(Task.class))).thenReturn(6L);
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(filteredPageItems);

        // Test filtering by due date
        LocalDate dueDateAfter = LocalDate.of(2024, 12, 17);
        LocalDate dueDateBefore = LocalDate.of(2024, 12, 29);
        TaskFilter taskFilter = new TaskFilter(null, dueDateAfter, dueDateBefore, null);
        PagedResponse<Task> result = taskService.getAllTasks(taskFilter, 1, 2);

        assertEquals(6, result.getTotalElements());
        assertEquals(List.of(task5, task6), result.getItems());
    }

    @Test
    public void testGetAllTasksWithSortAscending() {
        Task task2 = new Task("2", "Task 2", LocalDate.of(2024, 12, 20), Task.Status.IN_PROGRESS);
        Task task1 = new Task("1", "Task 1", LocalDate.of(2024, 12, 25), Task.Status.PENDING);
        Task task3 = new Task("3", "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);
        List<Task> pageItems = List.of(task2, task1, task3); // dueDate ascending

        when(mongoTemplate.count(any(Query.class), eq(Task.class))).thenReturn(3L);
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(pageItems);

        TaskFilter taskFilter = new TaskFilter(null, null, null, TaskFilter.SortDirection.ASC);
        PagedResponse<Task> result = taskService.getAllTasks(taskFilter, 0, 10);

        assertEquals(3, result.getTotalElements());
        assertEquals(List.of(task2, task1, task3), result.getItems());
    }

    @Test
    public void testGetAllTasksWithSortDescending() {
        Task task3 = new Task("3", "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);
        Task task1 = new Task("1", "Task 1", LocalDate.of(2024, 12, 25), Task.Status.PENDING);
        Task task2 = new Task("2", "Task 2", LocalDate.of(2024, 12, 20), Task.Status.IN_PROGRESS);
        List<Task> pageItems = List.of(task3, task1, task2); // dueDate descending

        when(mongoTemplate.count(any(Query.class), eq(Task.class))).thenReturn(3L);
        when(mongoTemplate.find(any(Query.class), eq(Task.class))).thenReturn(pageItems);

        TaskFilter taskFilter = new TaskFilter(null, null, null, TaskFilter.SortDirection.DESC);
        PagedResponse<Task> result = taskService.getAllTasks(taskFilter, 0, 10);

        assertEquals(3, result.getTotalElements());
        assertEquals(List.of(task3, task1, task2), result.getItems());
    }
}