package com.br.notification.domain.factory;

import com.br.notification.domain.enums.NotificationType;
import com.br.notification.domain.enums.ProviderType;
import com.br.notification.domain.strategy.AwsEmailNotification;
import com.br.notification.domain.strategy.AwsSmsNotification;
import com.br.notification.domain.strategy.contract.INotification;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {
  private final ApplicationContext applicationContext;

  private Map<ProviderType, Map<NotificationType, INotification>> factories;

  public NotificationFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @PostConstruct
  private void init() {
    this.factories =
        Map.of(
            ProviderType.AWS,
            Map.of(
                NotificationType.EMAIL,
                applicationContext.getBean("awsEmailNotification", AwsEmailNotification.class),
                NotificationType.SMS,
                applicationContext.getBean("awsSmsNotification", AwsSmsNotification.class)));
  }

  public INotification create(ProviderType providerType, NotificationType notificationType) {
    return Optional.ofNullable(this.factories.get(providerType))
        .map(
            factory ->
                Optional.ofNullable(factory.get(notificationType))
                    .orElseThrow(
                        () ->
                            new IllegalArgumentException(
                                String.format(
                                    "Notification type not found %s", notificationType.name()))))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format("Provider type not found %s", providerType.name())));
  }
}
