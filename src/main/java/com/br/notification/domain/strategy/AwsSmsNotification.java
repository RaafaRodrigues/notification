package com.br.notification.domain.strategy;

import com.br.notification.domain.dto.NotificationEventDTO;
import com.br.notification.domain.dto.SmsMessageDTO;
import com.br.notification.domain.exception.NotificationConsumerException;
import com.br.notification.domain.security.CryptoUtil;
import com.br.notification.domain.strategy.contract.INotification;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Component("awsSmsNotification")
@Log4j2
public class AwsSmsNotification implements INotification {
  private final SnsClient snsClient;

  private final CryptoUtil cryptoUtil;

  public AwsSmsNotification(SnsClient snsClient, CryptoUtil cryptoUtil) {
    this.snsClient = snsClient;
    this.cryptoUtil = cryptoUtil;
  }

  @Retryable(
      retryFor = {SnsException.class},
      backoff = @Backoff(delay = 2000, multiplier = 2))
  @Override
  public boolean send(NotificationEventDTO notificationEventDTO) {
    SmsMessageDTO sms = (SmsMessageDTO) notificationEventDTO.getDetails();

    PublishRequest publishRequest =
        PublishRequest.builder()
            .phoneNumber(cryptoUtil.decrypt(sms.getNumber()))
            .message(cryptoUtil.decrypt(notificationEventDTO.getMessage()))
            .build();
    try {
      PublishResponse response = snsClient.publish(publishRequest);
      return Objects.nonNull(response.messageId()) && !response.messageId().isEmpty();
    } catch (SnsException e) {
      log.error("Error sending sms via SNS: {}", e.awsErrorDetails().errorMessage(), e);
      throw new NotificationConsumerException(
          String.format(
              "Error sending sms via SNS. clientId: %s", notificationEventDTO.getClientId()),
          e);
    }
  }
}
