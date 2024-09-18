package com.encora.taskmanager.service;

import com.encora.taskmanager.model.User;
import com.encora.taskmanager.model.UserAccount;

import javax.security.auth.login.CredentialNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.util.Optional;

public interface UserAccountService {
    User registerUser(UserAccount userAccount);

    Optional<UserAccount> findByUsername(String username);

    UserAccount saveUserAccount(UserAccount userAccount);

    User validateUserAccount(String username, String password) throws CredentialNotFoundException, FailedLoginException;
}