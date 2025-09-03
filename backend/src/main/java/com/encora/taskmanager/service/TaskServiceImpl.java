package com.encora.taskmanager.service;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.PagedResponse;
import com.encora.taskmanager.model.Task;
import com.encora.taskmanager.model.TaskFilter;
import com.encora.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final MongoTemplate mongoTemplate;

    public TaskServiceImpl(TaskRepository taskRepository, MongoTemplate mongoTemplate) {
        this.taskRepository = taskRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public PagedResponse<Task> getAllTasks(TaskFilter taskFilter, int page, int size) {
        try {
            Criteria criteria = buildCriteria(taskFilter);
            Pageable pageable = buildPageable(taskFilter, page, size);
            List<Task> tasks = queryTasks(criteria, pageable);
            long totalSize = queryTaskCount(criteria);

            return new PagedResponse<>(tasks, page, size, totalSize);
        } catch (Exception e) {
            final String message = "Error retrieving tasks";
            logger.error(message, e);
            throw new TaskManagerException(message, e);
        }
    }

    private Criteria buildCriteria(TaskFilter taskFilter) {
        if (taskFilter == null) {
            return null;
        }

        // Build query criteria based on filters
        List<Criteria> criteriaList = new ArrayList<>();

        if (taskFilter.statuses() != null && !taskFilter.statuses().isEmpty()) {
            criteriaList.add(Criteria.where("status").in(taskFilter.statuses()));
        }
        if (taskFilter.dueDateAfter() != null) {
            criteriaList.add(Criteria.where("dueDate").gte(taskFilter.dueDateAfter()));
        }
        if (taskFilter.dueDateBefore() != null) {
            criteriaList.add(Criteria.where("dueDate").lte(taskFilter.dueDateBefore()));
        }
        if (criteriaList.isEmpty()) {
            return null;
        }

        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }

    private Pageable buildPageable(TaskFilter taskFilter, int page, int size) {
        // Sorting: default by id ascending; if sortDirection provided, sort by dueDate
        Sort sort;
        if (taskFilter != null && taskFilter.sortDirection() != null) {
            sort = taskFilter.sortDirection() == TaskFilter.SortDirection.ASC
                    ? Sort.by(Sort.Direction.ASC, "dueDate")
                    : Sort.by(Sort.Direction.DESC, "dueDate");
        } else {
            sort = Sort.by(Sort.Direction.ASC, "id");
        }

        return PageRequest.of(page, size, sort);
    }

    private List<Task> queryTasks(Criteria criteria, Pageable pageable) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        query.with(pageable);

        // Execute query for items
        return mongoTemplate.find(query, Task.class);
    }

    private long queryTaskCount(Criteria criteria) {
        // Build count query without pagination and sorting
        Query countQuery = new Query();
        if (criteria != null) {
            countQuery.addCriteria(criteria);
        }
        return mongoTemplate.count(countQuery, Task.class);
    }

    @Override
    public Optional<Task> getTaskById(String id) {
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
    public void deleteTask(String id) {
        try {
            taskRepository.deleteById(id);
        } catch (Exception e) {
            final String message = "Error deleting task with ID " + id;
            logger.error(message, e);
            throw new TaskManagerException(message, e);
        }
    }
}