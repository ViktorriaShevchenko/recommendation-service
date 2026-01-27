package com.starbank.recommendation_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final JdbcTemplate jdbcTemplate;

    public UserService(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UUID> findSingleUserIdByUsername(String username) {
        try {
            username = username.trim().toLowerCase();

            String sql = "SELECT id FROM users WHERE LOWER(username) = ?";

            List<UUID> results = jdbcTemplate.query(
                    sql,
                    (rs, rowNum) -> UUID.fromString(rs.getString("id")),
                    username
            );

            if (results.size() == 1) {
                return java.util.Optional.of(results.get(0));
            } else {
                return java.util.Optional.empty();
            }

        } catch (Exception e) {
            log.error("Error finding user by username: {}", username, e);
            return java.util.Optional.empty();
        }
    }

    public String getUserFullName(UUID userId) {
        try {
            log.debug("Getting full name for user ID: {}", userId);

            String sql = "SELECT first_name, last_name FROM users WHERE id = ?";

            return jdbcTemplate.query(sql, rs -> {
                if (rs.next()) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");

                    if (firstName != null && lastName != null) {
                        return firstName + " " + lastName;
                    } else if (firstName != null) {
                        return firstName;
                    } else {
                        return lastName;
                    }
                }
                return null;
            }, userId.toString());

        } catch (Exception e) {
            log.error("Error getting user full name for ID: {}", userId, e);
            return null;
        }
    }
}