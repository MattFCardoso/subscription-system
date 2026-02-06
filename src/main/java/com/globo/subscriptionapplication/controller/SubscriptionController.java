package com.globo.subscriptionapplication.controller;

import com.globo.subscriptionapplication.domain.dto.request.CreateSubscriptionRequest;
import com.globo.subscriptionapplication.domain.dto.response.SubscriptionResponse;
import com.globo.subscriptionapplication.service.interfaces.SubscriptionService;
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

    //todo fix this seems like it's missing redis just like the above one
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

//    @PutMapping("/{subscriptionId}/suspend")
//    @Operation(summary = "Suspend a subscription")
//    public ResponseEntity<Subscription> suspendSubscription(@PathVariable UUID subscriptionId) {
//        log.info("Suspending subscription: {}", subscriptionId);
//
//        try {
//            Subscription suspendedSubscription = subscriptionServiceImpl.suspendSubscription(subscriptionId);
//            return ResponseEntity.ok(suspendedSubscription);
//        } catch (RuntimeException e) {
//            log.error("Error suspending subscription: {}", e.getMessage());
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PutMapping("/{subscriptionId}/plan")
//    @Operation(summary = "Update subscription plan")
//    public ResponseEntity<Subscription> updateSubscriptionPlan(
//            @PathVariable UUID subscriptionId,
//            @Valid @RequestBody UpdatePlanRequest request) {
//        log.info("Updating plan for subscription: {} to: {}", subscriptionId, request.getPlan());
//
//        try {
//            Subscription updatedSubscription = subscriptionServiceImpl.updatePlan(subscriptionId, request.getPlan());
//            return ResponseEntity.ok(updatedSubscription);
//        } catch (RuntimeException e) {
//            log.error("Error updating subscription plan: {}", e.getMessage());
//            return ResponseEntity.badRequest().build();
//        }
//    }

    // ========================
    // PAYMENT MANAGEMENT ENDPOINTS
    // ========================
//
//    @GetMapping("/{subscriptionId}/payments")
//    @Operation(summary = "Get payment history for a subscription")
//    public ResponseEntity<List<Payment>> getPaymentHistory(@PathVariable UUID subscriptionId) {
//        log.info("Fetching payment history for subscription: {}", subscriptionId);
//
//        List<Payment> payments = paymentServiceImpl.getPaymentHistory(subscriptionId);
//        return ResponseEntity.ok(payments);
//    }
//
//    @GetMapping("/payments/failed")
//    @Operation(summary = "Get all failed payments")
//    public ResponseEntity<List<Payment>> getFailedPayments() {
//        log.info("Fetching all failed payments");
//
//        List<Payment> failedPayments = paymentServiceImpl.getFailedPayments();
//        return ResponseEntity.ok(failedPayments);
//    }
//
//    @GetMapping("/payments/range")
//    @Operation(summary = "Get payments by date range")
//    public ResponseEntity<List<Payment>> getPaymentsByDateRange(
//            @Parameter(description = "Start date (yyyy-MM-dd HH:mm:ss)")
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
//            @Parameter(description = "End date (yyyy-MM-dd HH:mm:ss)")
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
//
//        log.info("Fetching payments between {} and {}", startDate, endDate);
//
//        List<Payment> payments = paymentServiceImpl.getPaymentsByDateRange(startDate, endDate);
//        return ResponseEntity.ok(payments);
//    }
//
//    // ========================
//    // RENEWAL MANAGEMENT ENDPOINTS
//    // ========================
//
//    @PostMapping("/{subscriptionId}/renew")
//    @Operation(summary = "Manually trigger subscription renewal")
//    public ResponseEntity<String> manualRenewal(@PathVariable UUID subscriptionId) {
//        log.info("Manually triggering renewal for subscription: {}", subscriptionId);
//
//        try {
//            renewalServiceImpl.processManualRenewal(subscriptionId);
//            return ResponseEntity.ok("Renewal process initiated for subscription: " + subscriptionId);
//        } catch (RuntimeException e) {
//            log.error("Error processing manual renewal: {}", e.getMessage());
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }
//
//    // ========================
//    // UTILITY ENDPOINTS
//    // ========================
//
//    @GetMapping("/plans")
//    @Operation(summary = "Get all available plans")
//    public ResponseEntity<PlanEnum[]> getAvailablePlans() {
//        log.info("Fetching all available plans");
//
//        return ResponseEntity.ok(PlanEnum.values());
//    }
//
//    @GetMapping("/health")
//    @Operation(summary = "Health check endpoint")
//    public ResponseEntity<String> healthCheck() {
//        return ResponseEntity.ok("Subscription Service is running");
//    }
}
