package com.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class RecommendationDTO {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("text")
    private String text;

    public RecommendationDTO() {
        this.id = UUID.randomUUID();
    }

    public RecommendationDTO(String name, String text) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.text = text;
    }

    public RecommendationDTO(UUID id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
