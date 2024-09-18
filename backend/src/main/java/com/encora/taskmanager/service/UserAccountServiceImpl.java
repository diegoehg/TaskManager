package com.encora.taskmanager.service;

import com.encora.taskmanager.exception.AccountLockedException;
import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.User;
import com.encora.taskmanager.model.UserAccount;
import com.encora.taskmanager.repository.UserAccountRepository;
import com.encora.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private static final int BLOCK_TIME_MINUTES = 15;
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(UserAccount userAccount) {
        try {
            User user = new User(null, userAccount.getUsername());
            user = userRepository.save(user);
            userAccount.setUserId(user.id());
            userAccountRepository.save(userAccount);
            return user;
        } catch (Exception e) {
            LOGGER.error("Error registering user: {}", userAccount, e);
            throw new TaskManagerException("Error registering user", e);
        }
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        try {
            return userAccountRepository.findByUsername(username);
        } catch (Exception e) {
            LOGGER.error("Error retrieving user with username: {}", username, e);
            throw new TaskManagerException("Error retrieving user", e);
        }
    }

    @Override
    public UserAccount saveUserAccount(UserAccount userAccount) {
        try {
            return userAccountRepository.save(userAccount);
        } catch (Exception e) {
            LOGGER.error("Error saving user account: {}", userAccount, e);
            throw new TaskManagerException("Error saving user account", e);
        }
    }

    @Override
    public User validateUserAccount(String username, String password) throws CredentialNotFoundException, FailedLoginException {
        UserAccount userAccount = findByUsername(username)
                .orElseThrow(() -> new CredentialNotFoundException("No user found with those credentials"));

        if (isUserAccountLocked(userAccount)) {
            throw new AccountLockedException("Account locked. Please try again 15 minutes later.");
        }

        if (userAccount.getPassword().equals(password)) {
            resetFailedLoginAttempts(userAccount);
            return userRepository.findById(userAccount.getUserId())
                    .orElseThrow(() -> new CredentialNotFoundException("No user found with those credentials"));
        } else {
            handleFailedLoginAttempt(userAccount);
            throw new FailedLoginException("Invalid username or password");
        }
    }

    private boolean isUserAccountLocked(UserAccount userAccount) {
        return userAccount.getFailedLoginAttempts() >= MAX_FAILED_LOGIN_ATTEMPTS &&
                (userAccount.getTimeAfterOtherAttempt() == null || userAccount.getTimeAfterOtherAttempt().isAfter(LocalDateTime.now()));
    }

    private void handleFailedLoginAttempt(UserAccount userAccount) {
        userAccount.setTimeAfterOtherAttempt(LocalDateTime.now().plusMinutes(BLOCK_TIME_MINUTES));
        userAccount.setFailedLoginAttempts(userAccount.getFailedLoginAttempts() + 1);
        userAccountRepository.save(userAccount);
    }

    private void resetFailedLoginAttempts(UserAccount userAccount) {
        userAccount.setTimeAfterOtherAttempt(null);
        userAccount.setFailedLoginAttempts(0);
        userAccountRepository.save(userAccount);
    }
}