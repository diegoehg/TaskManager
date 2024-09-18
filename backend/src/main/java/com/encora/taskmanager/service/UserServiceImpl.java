package com.encora.taskmanager.service;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.User;
import com.encora.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<User> getUserById(Long id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            LOGGER.error("Error retrieving user with ID: {}", id, e);
            throw new TaskManagerException("Error retrieving user", e);
        }
    }

    @Override
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            LOGGER.error("Error saving user: {}", user, e);
            throw new TaskManagerException("Error saving user", e);
        }
    }
}