package com.br.notification.domain.dto;

import com.br.notification.domain.enums.NotificationType;
import com.br.notification.domain.enums.ProviderType;
import lombok.Data;

@Data
public class NotificationEventDTO {
  private String clientId;
  private String clientName;
  private String message;
  private NotificationType type;
  private NotificationMessageDTO details;
  private ProviderType provider;
}
