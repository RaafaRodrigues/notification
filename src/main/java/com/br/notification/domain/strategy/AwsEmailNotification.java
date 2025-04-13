package com.br.notification.domain.strategy;

import com.br.notification.domain.dto.EmailMessageDTO;
import com.br.notification.domain.dto.NotificationEventDTO;
import com.br.notification.domain.exception.NotificationConsumerException;
import com.br.notification.domain.security.CryptoUtil;
import com.br.notification.domain.strategy.contract.INotification;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Component("awsEmailNotification")
@Log4j2
public class AwsEmailNotification implements INotification {

  @Value("${aws.sender.email}")
  private String emailSender;

  private final SesClient sesClient;

  private final CryptoUtil cryptoUtil;

  public AwsEmailNotification(SesClient sesClient, CryptoUtil cryptoUtil) {
    this.sesClient = sesClient;
    this.cryptoUtil = cryptoUtil;
  }

  @Retryable(
      retryFor = {SesException.class},
      backoff = @Backoff(delay = 2000, multiplier = 2))
  @Override
  public boolean send(NotificationEventDTO notificationEventDTO) {
    EmailMessageDTO email = (EmailMessageDTO) notificationEventDTO.getDetails();
    log.info(email);
    SendEmailRequest request =
        SendEmailRequest.builder()
            .destination(
                destination -> destination.toAddresses(cryptoUtil.decrypt(email.getDestination())))
            .message(
                msg ->
                    msg.subject(sub -> sub.data(cryptoUtil.decrypt(email.getSubject())))
                        .body(
                            body ->
                                body.text(
                                    txt ->
                                        txt.data(
                                            cryptoUtil.decrypt(
                                                notificationEventDTO.getMessage())))))
            .source(emailSender)
            .build();

    try {
      SendEmailResponse response = sesClient.sendEmail(request);
      return Objects.nonNull(response.messageId()) && !response.messageId().isEmpty();
    } catch (SesException e) {
      log.error("Error sending email via SES: {}", e.awsErrorDetails().errorMessage(), e);
      throw new NotificationConsumerException(
          String.format(
              "Error sending sms via SNS. clientId: %s", notificationEventDTO.getClientId()),
          e);
    }
  }
}
