//package com.globo.subscriptionapplication.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.AmqpTemplate;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@Slf4j
//public class MessagingConfig {
//
//    /**
//     * Mock AmqpTemplate quando RabbitMQ não estiver disponível
//     */
//    @Bean
//    @ConditionalOnMissingBean(AmqpTemplate.class)
//    public AmqpTemplate mockAmqpTemplate() {
//        log.warn("RabbitMQ não está disponível. Usando implementação mock para AmqpTemplate.");
//
//        return new AmqpTemplate() {
//            @Override
//            public void send(String routingKey, org.springframework.amqp.core.Message message) {
//                log.debug("Mock send: routingKey={}", routingKey);
//            }
//
//            @Override
//            public void send(String exchange, String routingKey, org.springframework.amqp.core.Message message) {
//                log.debug("Mock send: exchange={}, routingKey={}", exchange, routingKey);
//            }
//
//            @Override
//            public void convertAndSend(Object message) {
//                log.debug("Mock convertAndSend: message={}", message);
//            }
//
//            @Override
//            public void convertAndSend(String routingKey, Object message) {
//                log.debug("Mock convertAndSend: routingKey={}, message={}", routingKey, message);
//            }
//
//            @Override
//            public void convertAndSend(String exchange, String routingKey, Object message) {
//                log.debug("Mock convertAndSend: exchange={}, routingKey={}, message={}", exchange, routingKey, message);
//            }
//
//            @Override
//            public void convertAndSend(Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor) {
//                log.debug("Mock convertAndSend with post processor: message={}", message);
//            }
//
//            @Override
//            public void convertAndSend(String routingKey, Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor) {
//                log.debug("Mock convertAndSend with post processor: routingKey={}, message={}", routingKey, message);
//            }
//
//            @Override
//            public void convertAndSend(String exchange, String routingKey, Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor) {
//                log.debug("Mock convertAndSend with post processor: exchange={}, routingKey={}, message={}", exchange, routingKey, message);
//            }
//
//            // Métodos de receive retornam null (não implementados no mock)
//            @Override public org.springframework.amqp.core.Message receive() { return null; }
//            @Override public org.springframework.amqp.core.Message receive(String queueName) { return null; }
//            @Override public org.springframework.amqp.core.Message receive(long timeoutMillis) { return null; }
//            @Override public org.springframework.amqp.core.Message receive(String queueName, long timeoutMillis) { return null; }
//            @Override public Object receiveAndConvert() { return null; }
//            @Override public Object receiveAndConvert(String queueName) { return null; }
//            @Override public Object receiveAndConvert(long timeoutMillis) { return null; }
//            @Override public Object receiveAndConvert(String queueName, long timeoutMillis) { return null; }
//            @Override public <T> T receiveAndConvert(org.springframework.core.ParameterizedTypeReference<T> type) { return null; }
//            @Override public <T> T receiveAndConvert(String queueName, org.springframework.core.ParameterizedTypeReference<T> type) { return null; }
//            @Override public <T> T receiveAndConvert(long timeoutMillis, org.springframework.core.ParameterizedTypeReference<T> type) { return null; }
//            @Override public <T> T receiveAndConvert(String queueName, long timeoutMillis, org.springframework.core.ParameterizedTypeReference<T> type) { return null; }
//            @Override public Object sendAndReceive(org.springframework.amqp.core.Message message) { return null; }
//            @Override public Object sendAndReceive(String routingKey, org.springframework.amqp.core.Message message) { return null; }
//            @Override public Object sendAndReceive(String exchange, String routingKey, org.springframework.amqp.core.Message message) { return null; }
//            @Override public Object convertSendAndReceive(Object message) { return null; }
//            @Override public Object convertSendAndReceive(String routingKey, Object message) { return null; }
//            @Override public Object convertSendAndReceive(String exchange, String routingKey, Object message) { return null; }
//            @Override public Object convertSendAndReceive(Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor) { return null; }
//            @Override public Object convertSendAndReceive(String routingKey, Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor) { return null; }
//            @Override public Object convertSendAndReceive(String exchange, String routingKey, Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor) { return null; }
//            @Override public <T> T convertSendAndReceive(Object message, org.springframework.core.ParameterizedTypeReference<T> responseType) { return null; }
//            @Override public <T> T convertSendAndReceive(String routingKey, Object message, org.springframework.core.ParameterizedTypeReference<T> responseType) { return null; }
//            @Override public <T> T convertSendAndReceive(String exchange, String routingKey, Object message, org.springframework.core.ParameterizedTypeReference<T> responseType) { return null; }
//            @Override public <T> T convertSendAndReceive(Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor, org.springframework.core.ParameterizedTypeReference<T> responseType) { return null; }
//            @Override public <T> T convertSendAndReceive(String routingKey, Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor, org.springframework.core.ParameterizedTypeReference<T> responseType) { return null; }
//            @Override public <T> T convertSendAndReceive(String exchange, String routingKey, Object message, org.springframework.amqp.core.MessagePostProcessor messagePostProcessor, org.springframework.core.ParameterizedTypeReference<T> responseType) { return null; }
//        };
//    }
//}
