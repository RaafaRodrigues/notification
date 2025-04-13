package com.br.notification.domain.service;

import com.br.notification.domain.dto.NotificationEventDTO;
import com.br.notification.domain.factory.NotificationFactory;
import com.br.notification.domain.strategy.contract.INotification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private final NotificationFactory notificationFactory;

  public NotificationService(NotificationFactory notificationFactory) {
    this.notificationFactory = notificationFactory;
  }

  public void processNotification(NotificationEventDTO notificationEventDTO) {
    INotification notification =
        notificationFactory.create(
            notificationEventDTO.getProvider(), notificationEventDTO.getType());

    notification.send(notificationEventDTO);
  }
}
