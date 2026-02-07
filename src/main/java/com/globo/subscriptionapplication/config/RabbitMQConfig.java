package com.globo.subscriptionapplication.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    // Exchanges
    public static final String RENEWAL_EXCHANGE = "renewal.exchange";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    // Queues
    public static final String RENEWAL_QUEUE = "renewal.queue";
    public static final String PAYMENT_QUEUE = "payment.queue";
    public static final String RENEWAL_RETRY_QUEUE = "renewal.retry.queue";
    public static final String NOTIFICATION_SUCCESS_QUEUE = "notification.success.queue";
    public static final String NOTIFICATION_FAILED_QUEUE = "notification.failed.queue";
    // Routing Keys
    public static final String RENEWAL_KEY = "renewal.key";
    public static final String RENEWAL_RETRY_KEY = "renewal.retry";
    public static final String NOTIFICATION_SUCCESS_KEY = "renewal.success";
    public static final String NOTIFICATION_FAILED_KEY = "renewal.failed";
    // Exchanges
    @Bean
    public DirectExchange renewalExchange() {
        return new DirectExchange(RENEWAL_EXCHANGE);
    }
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }
    // Queues
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(PAYMENT_QUEUE).build();
    }
    @Bean
    public Queue renewalQueue() {
        return QueueBuilder.durable(RENEWAL_QUEUE).build();
    }
    @Bean
    public Queue renewalRetryQueue() {
        return QueueBuilder.durable(RENEWAL_RETRY_QUEUE)
                .withArgument("x-message-ttl", 60000) // 1 minuto de delay
                .withArgument("x-dead-letter-exchange", RENEWAL_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", RENEWAL_KEY)
                .build();
    }
    @Bean
    public Queue notificationSuccessQueue() {
        return QueueBuilder.durable(NOTIFICATION_SUCCESS_QUEUE).build();
    }
    @Bean
    public Queue notificationFailedQueue() {
        return QueueBuilder.durable(NOTIFICATION_FAILED_QUEUE).build();
    }
    // Bindings
    @Bean
    public Binding renewalBinding() {
        return BindingBuilder.bind(renewalQueue()).to(renewalExchange()).with(RENEWAL_KEY);
    }
    @Bean
    public Binding renewalRetryBinding() {
        return BindingBuilder.bind(renewalRetryQueue()).to(renewalExchange()).with(RENEWAL_RETRY_KEY);
    }
    @Bean
    public Binding notificationSuccessBinding() {
        return BindingBuilder.bind(notificationSuccessQueue()).to(notificationExchange()).with(NOTIFICATION_SUCCESS_KEY);
    }
    @Bean
    public Binding notificationFailedBinding() {
        return BindingBuilder.bind(notificationFailedQueue()).to(notificationExchange()).with(NOTIFICATION_FAILED_KEY);
    }
    // Message Converter
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    // RabbitTemplate
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setDefaultReceiveQueue(RENEWAL_QUEUE);
        return template;
    }
    // Listener Container Factory
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(1);
        return factory;
    }
}