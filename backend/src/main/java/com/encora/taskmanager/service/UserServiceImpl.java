package com.encora.taskmanager.service;

import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.User;
import com.encora.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            LOGGER.error("Error registering user: {}", user, e);
            throw new TaskManagerException("Error registering user", e);
        }
    }

    @Override
    public void validateUser(String username, String password) throws TaskManagerException {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new TaskManagerException("Invalid username or password", null));

            if (!user.password().equals(password)) {
                throw new TaskManagerException("Invalid username or password", null);
            }
        } catch (Exception e) {
            LOGGER.error("Error validating user: {}", username, e);
            throw new TaskManagerException("Error validating user", e);
        }
    }
}