package com.encora.taskmanager.service;

import com.encora.taskmanager.model.User;

public interface UserService {
    User registerUser(User user);

    void validateUser(String username, String password);
}