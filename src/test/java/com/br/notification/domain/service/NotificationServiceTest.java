package com.br.notification.domain.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.br.notification.domain.dto.NotificationEventDTO;
import com.br.notification.domain.enums.NotificationType;
import com.br.notification.domain.enums.ProviderType;
import com.br.notification.domain.factory.NotificationFactory;
import com.br.notification.domain.strategy.contract.INotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class NotificationServiceTest {

  @Mock private NotificationFactory notificationFactory;

  @Mock private INotification notification;

  private NotificationService notificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new NotificationService(notificationFactory);
  }

  @Test
  void testProcessNotification_CallsFactoryAndSendNotification() {
    NotificationEventDTO notificationEventDTO = new NotificationEventDTO();
    notificationEventDTO.setProvider(ProviderType.AWS);
    notificationEventDTO.setType(NotificationType.EMAIL);

    when(notificationFactory.create(ProviderType.AWS, NotificationType.EMAIL))
        .thenReturn(notification);

    notificationService.processNotification(notificationEventDTO);

    verify(notificationFactory, times(1)).create(ProviderType.AWS, NotificationType.EMAIL);
    verify(notification, times(1)).send(notificationEventDTO);
  }

  @Test
  void testProcessNotification_WithDifferentNotificationType() {
    NotificationEventDTO notificationEventDTO = new NotificationEventDTO();
    notificationEventDTO.setProvider(ProviderType.AWS);
    notificationEventDTO.setType(NotificationType.SMS);

    when(notificationFactory.create(ProviderType.AWS, NotificationType.SMS))
        .thenReturn(notification);

    notificationService.processNotification(notificationEventDTO);

    verify(notificationFactory, times(1)).create(ProviderType.AWS, NotificationType.SMS);
    verify(notification, times(1)).send(notificationEventDTO);
  }

  @Test
  void testProcessNotification_ShouldThrowException_WhenFactoryReturnsNull() {
    NotificationEventDTO notificationEventDTO = new NotificationEventDTO();
    notificationEventDTO.setProvider(ProviderType.AWS);
    notificationEventDTO.setType(NotificationType.EMAIL);

    when(notificationFactory.create(ProviderType.AWS, NotificationType.EMAIL)).thenReturn(null);

    assertThrows(
        NullPointerException.class,
        () -> notificationService.processNotification(notificationEventDTO));
  }
}
