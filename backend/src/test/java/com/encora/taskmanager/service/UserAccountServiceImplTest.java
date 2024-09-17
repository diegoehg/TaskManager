package com.encora.taskmanager.service;

import com.encora.taskmanager.exception.InvalidCredentialsException;
import com.encora.taskmanager.exception.TaskManagerException;
import com.encora.taskmanager.model.User;
import com.encora.taskmanager.model.UserAccount;
import com.encora.taskmanager.repository.UserAccountRepository;
import com.encora.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserAccountServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Successful() {
        UserAccount userAccount = new UserAccount(1L, "testuser", "password", 1L, 0, null);
        User user = new User(1L, "testuser");

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);

        User registeredUser = userAccountService.registerUser(userAccount);

        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.username());
    }

    @Test
    void testValidateUserAccount_Successful() {
        UserAccount userAccount = new UserAccount(1L, "testuser", "password", 1L, 0, null);
        User user = new User(1L, "testuser");

        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User validatedUser = userAccountService.validateUserAccount("testuser", "password");

        assertNotNull(validatedUser);
        assertEquals("testuser", validatedUser.username());
    }

    @Test
    void testValidateUserAccount_NoUserFound() {
        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(InvalidCredentialsException.class, () ->
                userAccountService.validateUserAccount("testuser", "password")
        );
        assertEquals("No user found with those credentials", exception.getMessage());
    }

    @Test
    void testValidateUserAccount_InvalidPassword() {
        UserAccount userAccount = new UserAccount(1L, "testuser", "password", 1L, 0, null);

        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));

        Exception exception = assertThrows(TaskManagerException.class, () ->
                userAccountService.validateUserAccount("testuser", "wrongpassword")
        );
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testValidateUserAccount_AccountLocked() {
        UserAccount userAccount = new UserAccount(1L, "testuser", "password", 1L, 3, LocalDateTime.now().plusMinutes(10));

        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));

        Exception exception = assertThrows(TaskManagerException.class, () ->
                userAccountService.validateUserAccount("testuser", "password")
        );
        assertEquals("Account locked. Please try again 15 minutes later.", exception.getMessage());
    }
}