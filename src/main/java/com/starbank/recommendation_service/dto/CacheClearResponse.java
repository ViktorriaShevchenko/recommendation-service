package com.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CacheClearResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("caches_cleared")
    private int cachesCleared;

    public CacheClearResponse() {
    }

    public CacheClearResponse(boolean success, String message, int cachesCleared) {
        this.success = success;
        this.message = message;
        this.cachesCleared = cachesCleared;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCachesCleared() {
        return cachesCleared;
    }

    public void setCachesCleared(int cachesCleared) {
        this.cachesCleared = cachesCleared;
    }
}
