package com.br.notification.domain.consumer;

import com.br.notification.domain.config.KafkaPropertyHolder;
import com.br.notification.domain.dto.NotificationEventDTO;
import com.br.notification.domain.exception.NotificationConsumerException;
import com.br.notification.domain.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class NotificationConsumer {
  private final KafkaPropertyHolder kafkaPropertyHolder;
  private final ObjectMapper objectMapper;
  private final NotificationService notificationService;

  public NotificationConsumer(
      KafkaPropertyHolder kafkaPropertyHolder,
      ObjectMapper objectMapper,
      NotificationService notificationService) {
    this.kafkaPropertyHolder = kafkaPropertyHolder;
    this.objectMapper = objectMapper;
    this.notificationService = notificationService;
  }

  @RetryableTopic(backoff = @Backoff(delay = 20000), dltTopicSuffix = "-dlt.0", numPartitions = "8")
  @KafkaListener(
      topics = {
        "#{kafkaPropertyHolder.agreementCarriedOut}",
        "#{kafkaPropertyHolder.agreementBroken}",
        "#{kafkaPropertyHolder.pagCarrieOut}",
        "#{kafkaPropertyHolder.pagNotCarrieOut}"
      },
      groupId = "notification-consumer-group")
  public void consumeMessage(
      @Payload String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
    log.info("Received message from topic '{}': {}", topic, message);
    try {
      NotificationEventDTO notificationEventDTO =
          objectMapper.readValue(message, NotificationEventDTO.class);
      notificationService.processNotification(notificationEventDTO);
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize message: {}", message, e);
    } catch (Exception e) {
      log.error("Unexpected error while processing message: {}", message, e);
      throw new NotificationConsumerException(
          String.format("Error in process message %s", message), e);
    }
  }
}
