package com.encora.taskmanager.repository;

import com.encora.taskmanager.model.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends MongoRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
}