package com.starbank.recommendation_service.entity.dynamic;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleCondition {

    @NotBlank(message = "Query type is required")
    @Pattern(regexp = "USER_OF|ACTIVE_USER_OF|TRANSACTION_SUM_COMPARE|TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",
            message = "Query must be one of: USER_OF, ACTIVE_USER_OF, TRANSACTION_SUM_COMPARE, TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW")
    @JsonProperty("query")
    private String query;

    @NotNull(message = "Arguments list is required")
    @Size(min = 1, message = "Arguments must contain at least one element")
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