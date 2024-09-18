package com.encora.taskmanager.service;

import com.encora.taskmanager.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(Long id);

    User createUser(User user);
}