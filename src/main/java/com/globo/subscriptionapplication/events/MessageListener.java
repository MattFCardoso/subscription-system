package com.globo.subscriptionapplication.events;

import com.globo.subscriptionapplication.config.RabbitMQConfig;
import com.globo.subscriptionapplication.domain.enums.PaymentStatusEnum;
import com.globo.subscriptionapplication.domain.model.Subscription;
import com.globo.subscriptionapplication.domain.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageListener {

    private final SubscriptionRepository subscriptionRepository;
    private final MessageProducer messageProducer;

    @RabbitListener(queues = RabbitMQConfig.RENEWAL_QUEUE)
    public void processRenew(Subscription subscription) {

        if (PaymentStatusEnum.SUCCESS.equals(PaymentStatusEnum.randomPaymentStatus())) {
            subscription.renewSubscription();
            this.subscriptionRepository.save(subscription);
        }

        if (PaymentStatusEnum.FAILED.equals(PaymentStatusEnum.randomPaymentStatus())) {
            subscription.incrementRenewalAttempts();
            this.subscriptionRepository.save(subscription);
            messageProducer.sendFailedPaymentNotification(subscription.getUser());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_QUEUE)
    public void processNewSubscription(Subscription subscription) {

        if (PaymentStatusEnum.SUCCESS.equals(PaymentStatusEnum.randomPaymentStatus())) {
            subscription.startSubscription();
            this.subscriptionRepository.save(subscription);
        }

        if (PaymentStatusEnum.FAILED.equals(PaymentStatusEnum.randomPaymentStatus())) {
            subscription.cancelSubscription();
            this.subscriptionRepository.save(subscription);
            messageProducer.sendFailedPaymentNotification(subscription.getUser());
        }
    }
}