package com.starbank.recommendation_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildInfoResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    public BuildInfoResponse() {
    }

    public BuildInfoResponse(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
