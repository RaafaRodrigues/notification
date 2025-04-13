package com.br.notification.domain.strategy.contract;

import com.br.notification.domain.dto.NotificationEventDTO;

public interface INotification {
  boolean send(NotificationEventDTO message);
}
