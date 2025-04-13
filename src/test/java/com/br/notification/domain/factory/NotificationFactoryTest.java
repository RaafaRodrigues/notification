package com.br.notification.domain.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.br.notification.domain.enums.NotificationType;
import com.br.notification.domain.enums.ProviderType;
import com.br.notification.domain.strategy.AwsEmailNotification;
import com.br.notification.domain.strategy.AwsSmsNotification;
import com.br.notification.domain.strategy.contract.INotification;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class NotificationFactoryTest {

  @Mock private ApplicationContext applicationContext;

  @Mock private AwsEmailNotification awsEmailNotification;

  @Mock private AwsSmsNotification awsSmsNotification;

  @InjectMocks private NotificationFactory notificationFactory;

  @BeforeEach
  void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    when(applicationContext.getBean("awsEmailNotification", AwsEmailNotification.class))
        .thenReturn(awsEmailNotification);
    when(applicationContext.getBean("awsSmsNotification", AwsSmsNotification.class))
        .thenReturn(awsSmsNotification);

    Method initMethod = NotificationFactory.class.getDeclaredMethod("init");
    initMethod.setAccessible(true);
    initMethod.invoke(notificationFactory);
  }

  @Test
  void shouldReturnAwsEmailNotification() {
    INotification result = notificationFactory.create(ProviderType.AWS, NotificationType.EMAIL);
    assertEquals(awsEmailNotification, result);
  }

  @Test
  void shouldReturnAwsSmsNotification() {
    INotification result = notificationFactory.create(ProviderType.AWS, NotificationType.SMS);
    assertEquals(awsSmsNotification, result);
  }

  @Test
  void shouldThrowExceptionWhenNotificationTypeIsInvalid() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> notificationFactory.create(ProviderType.AWS, NotificationType.PUSH));
    assertTrue(exception.getMessage().contains("Notification type not found"));
  }

  @Test
  void shouldThrowExceptionWhenProviderTypeIsInvalid() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> notificationFactory.create(ProviderType.AZURE, NotificationType.EMAIL));
    assertTrue(exception.getMessage().contains("Provider type not found"));
  }
}
