package com.br.notification.domain.consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.br.notification.domain.config.KafkaPropertyHolder;
import com.br.notification.domain.dto.NotificationEventDTO;
import com.br.notification.domain.exception.NotificationConsumerException;
import com.br.notification.domain.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class NotificationConsumerTest {

  @Mock private KafkaPropertyHolder kafkaPropertyHolder;

  @Mock private ObjectMapper objectMapper;

  @Mock private NotificationService notificationService;

  @InjectMocks private NotificationConsumer notificationConsumer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldConsumeAndProcessMessageSuccessfully() throws Exception {
    String message = "{\"userId\":1,\"message\":\"Test\"}";
    String topic = "test-topic";
    NotificationEventDTO dto = new NotificationEventDTO();

    when(objectMapper.readValue(message, NotificationEventDTO.class)).thenReturn(dto);

    notificationConsumer.consumeMessage(message, topic);

    verify(objectMapper).readValue(message, NotificationEventDTO.class);
    verify(notificationService).processNotification(dto);
  }

  @Test
  void shouldLogErrorWhenJsonProcessingFails() throws Exception {
    String message = "invalid-json";
    String topic = "test-topic";

    when(objectMapper.readValue(message, NotificationEventDTO.class))
        .thenThrow(new JsonProcessingException("error") {});

    notificationConsumer.consumeMessage(message, topic);

    verify(notificationService, never()).processNotification(any());
  }

  @Test
  void shouldThrowCustomExceptionWhenUnexpectedErrorOccurs() throws Exception {
    String message = "{\"userId\":1,\"message\":\"Test\"}";
    String topic = "test-topic";
    NotificationEventDTO dto = new NotificationEventDTO();

    when(objectMapper.readValue(message, NotificationEventDTO.class)).thenReturn(dto);
    doThrow(new RuntimeException("unexpected")).when(notificationService).processNotification(dto);

    assertThrows(
        NotificationConsumerException.class,
        () -> {
          notificationConsumer.consumeMessage(message, topic);
        });

    verify(notificationService).processNotification(dto);
  }
}
