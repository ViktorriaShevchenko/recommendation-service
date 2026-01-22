package com.starbank.recommendation_service.controller;

import com.starbank.recommendation_service.dto.dynamic.DynamicRuleRequest;
import com.starbank.recommendation_service.dto.dynamic.DynamicRuleResponse;
import com.starbank.recommendation_service.service.dynamic.DynamicRuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DynamicRuleController {

    private final DynamicRuleService dynamicRuleService;

    @PostMapping("/rule")
    public ResponseEntity<DynamicRuleResponse> createRule(@RequestBody @Valid DynamicRuleRequest request) {
        try {
            return ResponseEntity.ok(dynamicRuleService.createRule(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rule")
    public ResponseEntity<List<DynamicRuleResponse>> getAllRules() {
        return ResponseEntity.ok(dynamicRuleService.getAllRules());
    }

    @DeleteMapping("/rule/{productId}")
    public ResponseEntity<Void> deleteRule(@PathVariable UUID productId) {
        try {
            dynamicRuleService.deleteRuleByProductId(productId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
