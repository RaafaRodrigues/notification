package com.br.notification.domain.exception;

public class NotificationConsumerException extends RuntimeException {
  public NotificationConsumerException(String message) {
    super(message);
  }

  public NotificationConsumerException(String message, Throwable cause) {
    super(message, cause);
  }
}
