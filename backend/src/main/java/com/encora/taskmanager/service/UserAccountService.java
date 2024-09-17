package com.encora.taskmanager.service;

import com.encora.taskmanager.model.User;
import com.encora.taskmanager.model.UserAccount;

public interface UserAccountService {
    User registerUser(UserAccount userAccount);

    User validateUserAccount(String username, String password);
}