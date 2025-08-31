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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

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
        Task task3 = new Task("3", "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);
        Task task4 = new Task("4", "Task 4", LocalDate.of(2024, 12, 12), Task.Status.COMPLETED);
        Task task5 = new Task("5", "Task 5", LocalDate.of(2024, 12, 15), Task.Status.IN_PROGRESS);
        List<Task> tasks = List.of(task1, task2, task3, task4, task5);


        when(taskRepository.count()).thenReturn(Long.valueOf(tasks.size()));
        when(taskRepository.findAll()).thenReturn(tasks);

        // Test filtering by status
        TaskFilter taskFilter = new TaskFilter(List.of(Task.Status.PENDING, Task.Status.IN_PROGRESS), null, null, null);
        PagedResponse<Task> result = taskService.getAllTasks(taskFilter, 0, 2);

        assertEquals(5, result.getTotalElements());
        assertEquals(List.of(task1, task2), result.getItems());
    }

    @Test
    public void testGetAllTasksWithDueDateFilter() {
        // Sample data
        Task task1 = new Task("1", "Task 1", LocalDate.of(2024, 12, 20), Task.Status.PENDING);
        Task task2 = new Task("2", "Task 2", LocalDate.of(2024, 12, 25), Task.Status.IN_PROGRESS);
        Task task3 = new Task("3", "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);
        Task task4 = new Task("4", "Task 4", LocalDate.of(2024, 12, 12), Task.Status.COMPLETED);
        Task task5 = new Task("5", "Task 5", LocalDate.of(2024, 12, 18), Task.Status.IN_PROGRESS);
        Task task6 = new Task("6", "Task 6", LocalDate.of(2024, 12, 29), Task.Status.COMPLETED);
        List<Task> tasks = List.of(task1, task2, task3, task4, task5, task6);

        when(taskRepository.count()).thenReturn(Long.valueOf(tasks.size()));
        when(taskRepository.findAll()).thenReturn(tasks);

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
        Task task1 = new Task("1", "Task 1", LocalDate.of(2024, 12, 25), Task.Status.PENDING);
        Task task2 = new Task("2", "Task 2", LocalDate.of(2024, 12, 20), Task.Status.IN_PROGRESS);
        Task task3 = new Task("3", "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);
        List<Task> tasks = List.of(task1, task2, task3);

        when(taskRepository.count()).thenReturn(Long.valueOf(tasks.size()));
        when(taskRepository.findAll()).thenReturn(tasks);

        TaskFilter taskFilter = new TaskFilter(null, null, null, TaskFilter.SortDirection.ASC);
        PagedResponse<Task> result = taskService.getAllTasks(taskFilter, 0, 10);

        assertEquals(3, result.getTotalElements());
        assertEquals(List.of(task2, task1, task3), result.getItems());
    }

    @Test
    public void testGetAllTasksWithSortDescending() {
        Task task1 = new Task("1", "Task 1", LocalDate.of(2024, 12, 25), Task.Status.PENDING);
        Task task2 = new Task("2", "Task 2", LocalDate.of(2024, 12, 20), Task.Status.IN_PROGRESS);
        Task task3 = new Task("3", "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);
        List<Task> tasks = List.of(task1, task2, task3);

        when(taskRepository.count()).thenReturn(Long.valueOf(tasks.size()));
        when(taskRepository.findAll()).thenReturn(tasks);

        TaskFilter taskFilter = new TaskFilter(null, null, null, TaskFilter.SortDirection.DESC);
        PagedResponse<Task> result = taskService.getAllTasks(taskFilter, 0, 10);

        assertEquals(3, result.getTotalElements());
        assertEquals(List.of(task3, task1, task2), result.getItems());
    }
}