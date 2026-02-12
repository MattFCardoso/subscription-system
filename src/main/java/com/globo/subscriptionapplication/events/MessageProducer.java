package com.globo.subscriptionapplication.events;

import com.globo.subscriptionapplication.config.RabbitMQConfig;
import com.globo.subscriptionapplication.domain.model.Subscription;
import com.globo.subscriptionapplication.domain.model.User;
import com.globo.subscriptionapplication.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MessageProducer {

    private final SubscriptionRepository subscriptionRepository;
    private final RabbitTemplate template;

    @Scheduled(cron = "0 */1 * * * *")
    public void triggerRenewEvent() {
        List<Subscription> subscriptions = subscriptionRepository.findAllExpired();
        subscriptions.forEach(this::sendToRenewSubscription);
    }

    private void sendToRenewSubscription(Subscription subscription) {
        template.convertAndSend(RabbitMQConfig.RENEWAL_QUEUE, subscription);
    }

    public void processSubscriptionPayment(Subscription subscription) {
        template.convertAndSend(RabbitMQConfig.PAYMENT_QUEUE, subscription);
    }

    public void sendFailedPaymentNotification(User user) {
        template.convertAndSend(RabbitMQConfig.NOTIFICATION_FAILED_KEY, "Dear user " + user.getName() + ", the payment " +
                "has failed. Please, try again.");
    }

}