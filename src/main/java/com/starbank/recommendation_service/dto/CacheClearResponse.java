package com.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CacheClearResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    public CacheClearResponse() {
    }

    public CacheClearResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
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
}