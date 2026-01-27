package com.starbank.recommendation_service.service;

import com.starbank.recommendation_service.repository.RecommendationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RecommendationsRepository recommendationsRepository;

    public UserService(RecommendationsRepository recommendationsRepository) {
        this.recommendationsRepository = recommendationsRepository;
    }

    public Optional<UUID> findSingleUserIdByUsername(String username) {
        log.debug("Finding user ID by username: {}", username);
        return recommendationsRepository.findSingleUserIdByUsername(username);
    }

    public String getUserFullName(UUID userId) {
        log.debug("Getting full name for user ID: {}", userId);
        return recommendationsRepository.getUserFullName(userId);
    }
}