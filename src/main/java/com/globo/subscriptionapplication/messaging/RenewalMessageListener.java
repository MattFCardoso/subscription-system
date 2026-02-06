//package com.globo.subscriptionapplication.messaging;
//
//import com.globo.subscriptionapplication.service.impl.RenewalServiceImpl;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class RenewalMessageListener {
//
//    private final RenewalServiceImpl renewalServiceImpl;
//
//    @RabbitListener(queues = "renewal.queue")
//    public void handleRenewalMessage(UUID subscriptionId) {
//        log.info("Received renewal message for subscription: {}", subscriptionId);
//
//        try {
//            renewalServiceImpl.processRenewal(subscriptionId);
//            log.info("Successfully processed renewal for subscription: {}", subscriptionId);
//        } catch (Exception e) {
//            log.error("Error processing renewal for subscription: {}", subscriptionId, e);
//        }
//    }
//
//    @RabbitListener(queues = "renewal.retry.queue")
//    public void handleRenewalRetryMessage(UUID subscriptionId) {
//        log.info("Received renewal retry message for subscription: {}", subscriptionId);
//
//        try {
//            renewalServiceImpl.processRenewal(subscriptionId);
//            log.info("Successfully processed retry renewal for subscription: {}", subscriptionId);
//        } catch (Exception e) {
//            log.error("Error processing retry renewal for subscription: {}", subscriptionId, e);
//        }
//    }
//}
