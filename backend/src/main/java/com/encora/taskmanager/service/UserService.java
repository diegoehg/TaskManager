package com.encora.taskmanager.service;

import com.encora.taskmanager.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(String id);

    User createUser(User user);
}