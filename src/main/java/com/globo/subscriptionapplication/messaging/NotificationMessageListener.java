package com.globo.subscriptionapplication.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationMessageListener {

    @RabbitListener(queues = "notification.success.queue")
    public void handleSuccessNotification(String message) {
        log.info("Renewal Success Notification: {}", message);
        // Aqui você poderia integrar com serviços de email, SMS, push notifications, etc.
        // Por exemplo: emailService.sendSuccessNotification(message);
    }

    @RabbitListener(queues = "notification.failed.queue")
    public void handleFailedNotification(String message) {
        log.warn("Renewal Failed Notification: {}", message);
        // Aqui você poderia integrar com serviços de alerta, email de administrador, etc.
        // Por exemplo: alertService.sendFailureAlert(message);
    }
}
