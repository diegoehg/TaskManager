package com.encora.taskmanager.model;

import java.util.Date;

public record Token(String username, Date issuedAt, Date expiration) {
}
