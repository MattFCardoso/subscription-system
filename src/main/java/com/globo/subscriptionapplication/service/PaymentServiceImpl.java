package com.globo.subscriptionapplication.service;

import com.globo.subscriptionapplication.domain.enums.PaymentStatusEnum;
import com.globo.subscriptionapplication.domain.model.Payment;
import com.globo.subscriptionapplication.domain.model.Subscription;
import com.globo.subscriptionapplication.domain.repository.PaymentRepository;
import com.globo.subscriptionapplication.service.impl.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final Random random = new Random();

    @Transactional
    public Payment processPayment(Subscription subscription, int attemptNumber) {
        log.info("Processing payment for subscription: {} - Attempt: {}",
                subscription.getSubscriptionId(), attemptNumber);

        Payment payment = Payment.builder()
                .subscription(subscription)
                .amount(subscription.getPlan().getPrice())
                .status(PaymentStatusEnum.PENDING)
                .attempt(attemptNumber)
                .paymentDate(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        // Simula processamento do pagamento com gateway externo
        boolean paymentSuccess = simulatePaymentProcessing(subscription.getPlan().getPrice());

        if (paymentSuccess) {
            payment.setStatus(PaymentStatusEnum.SUCCESS);
            log.info("Payment successful for subscription: {}", subscription.getSubscriptionId());
        } else {
            payment.setStatus(PaymentStatusEnum.FAILED);
            payment.setErrorMessage("Payment processing failed - insufficient funds or card declined");
            log.warn("Payment failed for subscription: {}", subscription.getSubscriptionId());
        }

        return paymentRepository.save(payment);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentHistory(UUID subscriptionId) {
        log.debug("Fetching payment history for subscription: {}", subscriptionId);
        return paymentRepository.findBySubscriptionOrderByPaymentDateDesc(subscriptionId);
    }

    @Transactional(readOnly = true)
    public List<Payment> getFailedPayments() {
        return paymentRepository.findByStatus(PaymentStatusEnum.FAILED);
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findByPaymentDateBetween(startDate, endDate);
    }

    /**
     * Simula o processamento do pagamento com gateway externo
     * Na implementação real, aqui seria feita a integração com Stripe, PayPal, etc.
     */
    private boolean simulatePaymentProcessing(BigDecimal amount) {
        try {
            // Simula tempo de processamento
            Thread.sleep(100);

            // 85% de chance de sucesso (simula taxa real de sucesso de pagamentos)
            return random.nextDouble() > 0.15;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted", e);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public boolean hasSuccessfulPayment(UUID subscriptionId) {
        List<Payment> successfulPayments = paymentRepository
                .findBySubscriptionSubscriptionIdAndStatus(subscriptionId, PaymentStatusEnum.SUCCESS);
        return !successfulPayments.isEmpty();
    }
}
