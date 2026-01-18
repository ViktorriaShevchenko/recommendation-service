package com.starbank.recommendation_service.entity.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleCondition {

    @JsonProperty("query")
    private String query;

    @JsonProperty("arguments")
    private List<String> arguments;

    @JsonProperty("negate")
    private boolean negate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleCondition that = (RuleCondition) o;
        return negate == that.negate &&
                Objects.equals(query, that.query) &&
                Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, arguments, negate);
    }
}