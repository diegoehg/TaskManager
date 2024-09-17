package com.encora.taskmanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public record User(
        @Id
        Long id,
        String username
) {
}