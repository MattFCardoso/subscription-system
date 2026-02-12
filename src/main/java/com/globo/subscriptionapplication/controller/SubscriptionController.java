package com.globo.subscriptionapplication.controller;

import com.globo.subscriptionapplication.domain.dto.request.CreateSubscriptionRequest;
import com.globo.subscriptionapplication.domain.dto.request.UpdatePlanRequest;
import com.globo.subscriptionapplication.domain.dto.response.SubscriptionResponse;
import com.globo.subscriptionapplication.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subscription Management", description = "APIs for managing subscriptions and users")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @Operation(summary = "Create a new subscription")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subscription created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or payment failed"),
            @ApiResponse(responseCode = "409", description = "User already has active subscription")})
    public ResponseEntity<SubscriptionResponse> createSubscription(@Valid @RequestBody CreateSubscriptionRequest request) {
        log.info("Creating subscription for user: {} with plan: {}", request.getEmail(), request.getPlan());
        SubscriptionResponse response = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{subscriptionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get subscription by ID")
    public ResponseEntity<SubscriptionResponse> getSubscription(@PathVariable UUID subscriptionId) {
        log.info("Fetching subscription: {}", subscriptionId);
        SubscriptionResponse response = subscriptionService.getSubscriptionById(subscriptionId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all subscriptions for a user")
    public ResponseEntity<SubscriptionResponse> getSubscriptionsByUser(@PathVariable UUID userId) {
        log.info("Fetching all subscriptions for user: {}", userId);
        SubscriptionResponse response = subscriptionService.getActiveSubscriptionByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{subscriptionId}")
    @Operation(summary = "Cancel a subscription")
    public ResponseEntity<Void> cancelSubscription(@PathVariable UUID subscriptionId) {
        log.info("Cancelling subscription: {}", subscriptionId);
        this.subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{subscriptionId}/plan")
    @Operation(summary = "Update subscription plan")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Plan updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid plan or subscription not active"),
            @ApiResponse(responseCode = "404", description = "Subscription not found")})
    public ResponseEntity<SubscriptionResponse> updateSubscriptionPlan(
            @PathVariable UUID subscriptionId,
            @Valid @RequestBody UpdatePlanRequest request) {
        log.info("Updating subscription plan: {} to new plan: {}", subscriptionId, request.getNewPlan());
        SubscriptionResponse response = subscriptionService.updateSubscriptionPlan(subscriptionId, request);
        return ResponseEntity.ok(response);
    }
}
