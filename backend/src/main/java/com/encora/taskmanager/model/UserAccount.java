package com.encora.taskmanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("userAccounts")
public class UserAccount {
    @Id
    private String id;

    private String username;

    private String password;

    private String userId;

    private int failedLoginAttempts;

    private LocalDateTime timeAfterOtherAttempt;

    public UserAccount(String id, String username, String password, String userId, int failedLoginAttempts, LocalDateTime timeAfterOtherAttempt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.failedLoginAttempts = failedLoginAttempts;
        this.timeAfterOtherAttempt = timeAfterOtherAttempt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getTimeAfterOtherAttempt() {
        return timeAfterOtherAttempt;
    }

    public void setTimeAfterOtherAttempt(LocalDateTime timeAfterOtherAttempt) {
        this.timeAfterOtherAttempt = timeAfterOtherAttempt;
    }
}