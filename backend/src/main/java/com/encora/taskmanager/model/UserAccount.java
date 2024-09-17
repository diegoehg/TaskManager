package com.encora.taskmanager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("userAccounts")
public class UserAccount {
    @Id
    private Long id;

    private String username;

    private String password;

    private Long userId;

    private int failedLoginAttempts;

    private LocalDateTime timeAfterOtherAttempt;

    public UserAccount(Long id, String username, String password, Long userId, int failedLoginAttempts, LocalDateTime timeAfterOtherAttempt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.failedLoginAttempts = failedLoginAttempts;
        this.timeAfterOtherAttempt = timeAfterOtherAttempt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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