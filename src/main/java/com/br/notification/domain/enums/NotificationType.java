package com.br.notification.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
  EMAIL("EMAIL"),
  SMS("SMS"),
  PUSH("PUSH");
  private final String name;

  NotificationType(String name) {
    this.name = name;
  }
}
