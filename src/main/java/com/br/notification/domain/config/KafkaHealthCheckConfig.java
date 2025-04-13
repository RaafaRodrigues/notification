package com.br.notification.domain.config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaHealthCheckConfig {

  private final KafkaAdmin kafkaAdmin;

  public KafkaHealthCheckConfig(KafkaAdmin kafkaAdmin) {
    this.kafkaAdmin = kafkaAdmin;
  }

  @Bean
  public HealthIndicator kafkaHealthIndicator() {
    return () -> {
      try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
        ListTopicsResult listTopicsResult =
            adminClient.listTopics(new ListTopicsOptions().listInternal(false));

        if (listTopicsResult.names().get(3, TimeUnit.SECONDS).isEmpty()) {
          return Health.down()
              .withDetail("message", "Kafka is accessible, but there are no topics.")
              .build();
        }

        return Health.up().withDetail("message", "Kafka is functioning correctly").build();
      } catch (TimeoutException e) {
        return Health.down(e).withDetail("error", "Timeout when connecting to Kafka").build();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return Health.down(e)
            .withDetail("error", "An interruption occurred while connecting to Kafka")
            .build();
      } catch (ExecutionException e) {
        return Health.down(e)
            .withDetail("error", "Kafka connection failed: " + e.getCause().getMessage())
            .build();
      } catch (Exception e) {
        return Health.down(e)
            .withDetail("error", "Unexpected error when connecting to Kafka: " + e.getMessage())
            .build();
      }
    };
  }
}
