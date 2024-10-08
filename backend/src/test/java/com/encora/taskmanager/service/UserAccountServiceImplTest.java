package com.encora.taskmanager.service;

import com.encora.taskmanager.model.User;
import com.encora.taskmanager.model.UserAccount;
import com.encora.taskmanager.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.CredentialNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserAccountServiceImplTest {

    @Mock
    private UserService userService;

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
        UserAccount userAccount = new UserAccount("1", "testuser", "password", "1", 0, null);
        User user = new User("1", "testuser");

        when(userService.createUser(any(User.class))).thenReturn(user);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(userAccount);

        User registeredUser = userAccountService.registerUser(userAccount);

        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.username());
    }

    @Test
    void testValidateUserAccount_Successful() throws Exception {
        UserAccount userAccount = new UserAccount("1", "testuser", "password", "1", 0, null);
        User user = new User("1", "testuser");

        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));
        when(userService.getUserById("1")).thenReturn(Optional.of(user));

        User validatedUser = userAccountService.validateUserAccount("testuser", "password");

        assertNotNull(validatedUser);
        assertEquals("testuser", validatedUser.username());
    }

    @Test
    void testValidateUserAccount_NoUserFound() {
        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(CredentialNotFoundException.class, () ->
                userAccountService.validateUserAccount("testuser", "password")
        );
        assertEquals("No user found with those credentials", exception.getMessage());
    }

    @Test
    void testValidateUserAccount_InvalidPassword() {
        UserAccount userAccount = new UserAccount("1", "testuser", "password", "1", 0, null);

        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));

        Exception exception = assertThrows(FailedLoginException.class, () ->
                userAccountService.validateUserAccount("testuser", "wrongpassword")
        );
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testValidateUserAccount_AccountLocked() {
        UserAccount userAccount = new UserAccount("1", "testuser", "password", "1", 3, LocalDateTime.now().plusMinutes(10));

        when(userAccountRepository.findByUsername("testuser")).thenReturn(Optional.of(userAccount));

        Exception exception = assertThrows(AccountLockedException.class, () ->
                userAccountService.validateUserAccount("testuser", "password")
        );
        assertEquals("Account locked. Please try again 15 minutes later.", exception.getMessage());
    }
}