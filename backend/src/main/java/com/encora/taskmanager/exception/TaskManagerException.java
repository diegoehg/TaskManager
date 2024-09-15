package com.encora.taskmanager.exception;

public class TaskManagerException extends RuntimeException {
    public TaskManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}