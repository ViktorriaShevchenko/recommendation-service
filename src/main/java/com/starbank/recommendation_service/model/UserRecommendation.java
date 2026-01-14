package com.starbank.recommendation_service.model;

import java.util.UUID;

public class UserRecommendation {
    private UUID id;
    private String name;
    private String description;
    private String type;

    public UserRecommendation() {}

    public UserRecommendation(String name, String description, String type) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public UserRecommendation(UUID id, String name, String description, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
