package com.br.notification.domain.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

  private final KafkaPropertyHolder kafkaPropertyHolder;

  public KafkaTopicConfig(KafkaPropertyHolder kafkaPropertyHolder) {
    this.kafkaPropertyHolder = kafkaPropertyHolder;
  }

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(
        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaPropertyHolder.getBootstrapServer());
    return new KafkaAdmin(configs);
  }
}
