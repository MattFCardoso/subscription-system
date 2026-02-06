package com.globo.subscriptionapplication.service;

import com.globo.subscriptionapplication.domain.model.Payment;
import com.globo.subscriptionapplication.domain.model.Subscription;
import com.globo.subscriptionapplication.service.impl.RenewalService;
import com.globo.subscriptionapplication.service.impl.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RenewalServiceImpl implements RenewalService {

    private final SubscriptionService subscriptionService;
    private final PaymentServiceImpl paymentServiceImpl;
    private final AmqpTemplate amqpTemplate;

    @Scheduled(cron = "0 0 9 * * *") // Executa todos os dias às 9h
    public void processExpiredSubscriptions() {
        log.info("Starting automatic renewal process for expired subscriptions");

//        LocalDate today = LocalDate.now();
////        List<Subscription> expiredSubscriptions = subscriptionServiceImpl.findExpiredSubscriptions(today);
//
//        log.info("Found {} expired subscriptions to process", expiredSubscriptions.size());
//
//        for (Subscription subscription : expiredSubscriptions) {
//            try {
//                processRenewal(subscription.getSubscriptionId());
//            } catch (Exception e) {
//                log.error("Error processing renewal for subscription: {}", subscription.getSubscriptionId(), e);
//            }
//        }

        log.info("Automatic renewal process completed");
    }

//    @Async
//    @Transactional
//    public void processRenewal(UUID subscriptionId) {
//        log.info("Processing renewal for subscription: {}", subscriptionId);
//
//        Subscription subscription = subscriptionService.getSubscriptionById(subscriptionId)
//                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));
//
//        if (subscription.getStatus() != SubscriptionStatusEnum.ATIVA) {
//            log.warn("Skipping renewal for non-active subscription: {}", subscriptionId);
//            return;
//        }
//
//        if (subscription.attemptsExceeded()) {
//            log.warn("Renewal attempts exceeded for subscription: {}", subscriptionId);
//            subscriptionServiceImpl.suspendSubscription(subscriptionId);
//            sendRenewalFailedNotification(subscription);
//            return;
//        }
//
//        int attemptNumber = subscription.getRenewalAttempts() + 1;
//
//        try {
//            // Processa o pagamento
//            Payment payment = paymentServiceImpl.processPayment(subscription, attemptNumber);
//
//            if (payment.getStatus() == PaymentStatusEnum.SUCCESS) {
//                // Pagamento bem-sucedido, renova a assinatura
//                LocalDate newExpirationDate = subscription.getExpirationDate().plusMonths(1);
//                subscriptionServiceImpl.renewSubscription(subscriptionId, newExpirationDate);
//
//                sendRenewalSuccessNotification(subscription, payment);
//                log.info("Subscription renewed successfully: {}", subscriptionId);
//
//            } else {
//                // Pagamento falhou, incrementa tentativas
//                subscriptionServiceImpl.incrementRenewalAttempts(subscriptionId);
//
//                sendRenewalAttemptFailedNotification(subscription, payment, attemptNumber);
//                log.warn("Renewal payment failed for subscription: {} - Attempt: {}", subscriptionId, attemptNumber);
//
//                // Se ainda não excedeu as tentativas, agenda nova tentativa
//                if (attemptNumber < 3) {
//                    scheduleRetryRenewal(subscriptionId);
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("Error processing renewal for subscription: {}", subscriptionId, e);
//            subscriptionServiceImpl.incrementRenewalAttempts(subscriptionId);
//
//            if (subscription.getRenewalAttempts() >= 2) { // Próxima tentativa será a 3ª
//                subscriptionServiceImpl.suspendSubscription(subscriptionId);
//                sendRenewalFailedNotification(subscription);
//            }
//        }
//    }

    @Async
    public void scheduleRetryRenewal(UUID subscriptionId) {
        log.info("Scheduling retry renewal for subscription: {}", subscriptionId);

        // Envia mensagem para fila de renovação com delay
        amqpTemplate.convertAndSend("renewal.exchange", "renewal.retry", subscriptionId);
    }

//    @Transactional
//    public void processManualRenewal(UUID subscriptionId) {
//        log.info("Processing manual renewal for subscription: {}", subscriptionId);
//
//        Subscription subscription = subscriptionServiceImpl.getSubscriptionById(subscriptionId)
//                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));
//
//        if (subscription.getStatus() == SubscriptionStatusEnum.CANCELADA) {
//            throw new RuntimeException("Cannot renew cancelled subscription");
//        }
//
//        // Reativa se estiver suspensa
//        if (subscription.getStatus() == SubscriptionStatusEnum.SUSPENSA) {
//            subscription.setStatus(SubscriptionStatusEnum.ATIVA);
//        }
//
//        processRenewal(subscriptionId);
//    }

    private void sendRenewalSuccessNotification(Subscription subscription, Payment payment) {
        log.debug("Sending renewal success notification for subscription: {}", subscription.getSubscriptionId());

        // Enviar notificação via RabbitMQ
        amqpTemplate.convertAndSend("notification.exchange", "renewal.success",
                String.format("Subscription %s renewed successfully. Payment ID: %s",
                        subscription.getSubscriptionId(), payment.getPaymentId()));
    }

    private void sendRenewalAttemptFailedNotification(Subscription subscription, Payment payment, int attemptNumber) {
        log.debug("Sending renewal attempt failed notification for subscription: {}", subscription.getSubscriptionId());

        amqpTemplate.convertAndSend("notification.exchange", "renewal.attempt.failed",
                String.format("Renewal attempt %d failed for subscription %s. Payment ID: %s",
                        attemptNumber, subscription.getSubscriptionId(), payment.getPaymentId()));
    }

    private void sendRenewalFailedNotification(Subscription subscription) {
        log.debug("Sending renewal failed notification for subscription: {}", subscription.getSubscriptionId());

        amqpTemplate.convertAndSend("notification.exchange", "renewal.failed",
                String.format("Subscription %s suspended due to failed renewal attempts",
                        subscription.getSubscriptionId()));
    }
}
