package com.demo.messaging;

import com.demo.contract.Fixture;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.WakeupException;
import com.demo.contract.Concept;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Concept(description = "A demo for Kafka messaging integration")
public class KafkaMessagingDemo {
    private static final KafkaProducer<String, String> producer;
    private static final KafkaConsumer<String, String> consumer;

    static {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:30093");
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(producerProps);

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:30093");
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        // Uncomment the following line to read messages from the beginning of the topic
        // consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(consumerProps);
    }

    // THis is used to synchronize teh consumer thread with the main thread on which the producer is running
    private final AtomicBoolean running = new AtomicBoolean(true);

    @Fixture(description = "Send messages to a Kafka topic")
    public void SendMessageToTopic() {
        consumer.subscribe(java.util.Collections.singletonList("mytopic"));

        var consumerThread = new Thread(() -> {
            try {
                while (running.get()) {
                    var records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.printf("Received message: %s%n", record.value());
                    }
                }
            } catch (WakeupException e) {
                // Expected when shutting down
                System.out.println("Consumer is shutting down...");
            } catch (Exception e) {
                System.err.println("Consumer error: " + e.getMessage());
            }
        });

        consumerThread.start();

        // Give consumer time to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Send messages
        for (int i = 1; i <= 10; i++) {
            producer.send(new ProducerRecord<>("mytopic", "Hello world from " + i));
            System.out.println("Sent message: " + i);
        }

        // Graceful shutdown
        running.set(false);
        consumer.wakeup(); // Wake up the consumer from poll()

        try {
            consumerThread.join(5000); // Wait up to 5 seconds for thread to finish
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Demo completed");
    }
}
