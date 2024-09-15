package com.encora.taskmanager.service;

import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import com.encora.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
        Task task1 = new Task(1L, "Task 1", LocalDate.of(2024, 12, 20), Task.Status.PENDING);
        Task task2 = new Task(2L, "Task 2", LocalDate.of(2024, 12, 25), Task.Status.IN_PROGRESS);
        Task task3 = new Task(3L, "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);
        Task task4 = new Task(4L, "Task 4", LocalDate.of(2024, 12, 12), Task.Status.COMPLETED);
        Task task5 = new Task(5L, "Task 5", LocalDate.of(2024, 12, 15), Task.Status.IN_PROGRESS);


        when(taskRepository.findAll()).thenReturn(List.of(task1, task2, task3, task4, task5));

        // Test filtering by status
        TaskFilter taskFilter = new TaskFilter(List.of(Task.Status.PENDING, Task.Status.IN_PROGRESS), null, null, null);
        Page<Task> result = taskService.getAllTasks(taskFilter, PageRequest.of(0, 2));

        assertEquals(2, result.getTotalElements());
        assertEquals(List.of(task1, task2), result.getContent());
    }

    @Test
    public void testGetAllTasksWithDueDateFilter() {
        // Sample data
        Task task1 = new Task(1L, "Task 1", LocalDate.of(2024, 12, 20), Task.Status.PENDING);
        Task task2 = new Task(2L, "Task 2", LocalDate.of(2024, 12, 25), Task.Status.IN_PROGRESS);
        Task task3 = new Task(3L, "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);
        Task task4 = new Task(4L, "Task 4", LocalDate.of(2024, 12, 12), Task.Status.COMPLETED);
        Task task5 = new Task(5L, "Task 5", LocalDate.of(2024, 12, 18), Task.Status.IN_PROGRESS);
        Task task6 = new Task(6L, "Task 6", LocalDate.of(2024, 12, 29), Task.Status.COMPLETED);


        when(taskRepository.findAll()).thenReturn(List.of(task1, task2, task3, task4, task5, task6));

        // Test filtering by due date
        LocalDate dueDateAfter = LocalDate.of(2024, 12, 17);
        LocalDate dueDateBefore = LocalDate.of(2024, 12, 29);
        TaskFilter taskFilter = new TaskFilter(null, dueDateAfter, dueDateBefore, null);
        Page<Task> result = taskService.getAllTasks(taskFilter, PageRequest.of(1, 2));

        assertEquals(4, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(List.of(task5, task6), result.getContent());
    }

    @Test
    public void testGetAllTasksWithSortAscending() {
        Task task1 = new Task(1L, "Task 1", LocalDate.of(2024, 12, 25), Task.Status.PENDING);
        Task task2 = new Task(2L, "Task 2", LocalDate.of(2024, 12, 20), Task.Status.IN_PROGRESS);
        Task task3 = new Task(3L, "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2, task3));

        TaskFilter taskFilter = new TaskFilter(null, null, null, Sort.Direction.ASC);
        Page<Task> result = taskService.getAllTasks(taskFilter, PageRequest.of(0, 10));

        assertEquals(3, result.getTotalElements());
        assertEquals(List.of(task2, task1, task3), result.getContent());
    }

    @Test
    public void testGetAllTasksWithSortDescending() {
        Task task1 = new Task(1L, "Task 1", LocalDate.of(2024, 12, 25), Task.Status.PENDING);
        Task task2 = new Task(2L, "Task 2", LocalDate.of(2024, 12, 20), Task.Status.IN_PROGRESS);
        Task task3 = new Task(3L, "Task 3", LocalDate.of(2024, 12, 30), Task.Status.COMPLETED);

        when(taskRepository.findAll()).thenReturn(List.of(task1, task2, task3));

        TaskFilter taskFilter = new TaskFilter(null, null, null, Sort.Direction.DESC);
        Page<Task> result = taskService.getAllTasks(taskFilter, PageRequest.of(0, 10));

        assertEquals(3, result.getTotalElements());
        assertEquals(List.of(task3, task1, task2), result.getContent());
    }
}