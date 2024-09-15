package com.encora.taskmanager.service;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import com.encora.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Page<Task> getAllTasks(TaskFilter taskFilter, Pageable pageable) {
        try {
            List<Task> tasks = taskRepository.findAll().stream()
                    .filter(task -> filterTaskByStatus(task, taskFilter))
                    .filter(task -> filterByDueDateAfter(task, taskFilter))
                    .filter(task -> filterByDueDateBefore(task, taskFilter))
                    .sorted(getTaskComparator(taskFilter))
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());

            return new PageImpl<>(tasks, pageable, tasks.size());
        } catch (Exception e) {
            final String message = "Error retrieving tasks";
            logger.error(message, e);
            throw new TaskManagerException(message, e);
        }
    }

    private boolean filterTaskByStatus(Task task, TaskFilter taskFilter) {
        return taskFilter.statuses() == null || taskFilter.statuses().isEmpty() ||
                taskFilter.statuses().contains(task.status());
    }

    private boolean filterByDueDateAfter(Task task, TaskFilter taskFilter) {
        return taskFilter.dueDateBefore() == null || task.dueDate().isAfter(taskFilter.dueDateAfter()) ||
                task.dueDate().isEqual(taskFilter.dueDateAfter());
    }

    private boolean filterByDueDateBefore(Task task, TaskFilter taskFilter) {
        return taskFilter.dueDateAfter() == null || task.dueDate().isBefore(taskFilter.dueDateBefore()) ||
                task.dueDate().isEqual(taskFilter.dueDateBefore());
    }

    private Comparator<Task> getTaskComparator(TaskFilter taskFilter) {
        if (taskFilter.sortDirection() == null) {
            return Comparator.comparing(Task::id);
        }

        Comparator<Task> dueDateComparator = Comparator.comparing(Task::dueDate);

        if (taskFilter.sortDirection().equals(Sort.Direction.ASC)) {
            return dueDateComparator;
        }

        return dueDateComparator.reversed();
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        try {
            return taskRepository.findById(id);
        } catch (Exception e) {
            final String message = "Error retrieving task with ID " + id;
            logger.error(message, e);
            throw new TaskManagerException(message, e);
        }
    }

    @Override
    public Task createTask(Task task) {
        try {
            return taskRepository.save(task);
        } catch (Exception e) {
            final String message = "Error creating task";
            logger.error(message, e);
            throw new TaskManagerException(message, e);
        }
    }

    @Override
    public Task updateTask(Task task) {
        try {
            return taskRepository.save(task);
        } catch (Exception e) {
            final String message = "Error updating task";
            logger.error(message, e);
            throw new TaskManagerException(message, e);
        }
    }

    @Override
    public void deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
        } catch (Exception e) {
            final String message = "Error deleting task with ID " + id;
            logger.error(message, e);
            throw new TaskManagerException(message, e);
        }
    }
}