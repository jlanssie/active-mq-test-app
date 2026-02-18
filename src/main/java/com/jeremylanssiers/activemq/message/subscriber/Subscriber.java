package com.jeremylanssiers.activemq.message.subscriber;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.JmsListener;

@Slf4j
@SpringBootApplication
public class Subscriber implements CommandLineRunner {
    private static final String ADDRESS_CONFIG = "${subscriber.jms.routing-context.address:messageAddress}";
    private static final String SUBSCRIPTION_CONFIG = "${subscriber.jms.subscription:topic}";

    @Value(ADDRESS_CONFIG)
    private String address;

    @Value(SUBSCRIPTION_CONFIG)
    private String topic;

    public static void main(String[] args) {
        SpringApplication.run(Subscriber.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Subscriber started. Subscribed to {}/{}", address, topic);
    }

    @JmsListener(destination = ADDRESS_CONFIG, subscription = SUBSCRIPTION_CONFIG)
    public void receiveMessage(Message message) {
        log.info("Received message: {}", message);

        try {
            if (message instanceof TextMessage textMessage) {
                String body = textMessage.getText();
                String corrId = message.getStringProperty("MESSAGE_CORRELATION_ID");

                log.info("Received Message: {}", body);
                log.info("Correlation ID: {}", corrId);
            } else {
                log.warn("Received non-text message: {}", message);
            }
        } catch (JMSException e) {
            log.error("Error processing JMS message", e);
        }
    }
}
