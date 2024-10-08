package com.encora.taskmanager.model;

public record GenericResponse<T>(Status status, String message, T data) {
    public enum Status {
        SUCCESS,
        FAILED
    }
}