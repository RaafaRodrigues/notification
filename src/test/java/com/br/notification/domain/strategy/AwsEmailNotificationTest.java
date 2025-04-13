package com.br.notification.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.br.notification.domain.dto.EmailMessageDTO;
import com.br.notification.domain.dto.NotificationEventDTO;
import com.br.notification.domain.exception.NotificationConsumerException;
import com.br.notification.domain.security.CryptoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

class AwsEmailNotificationTest {

  @Mock private SesClient sesClient;

  @Mock private CryptoUtil cryptoUtil;

  @InjectMocks private AwsEmailNotification awsEmailNotification;

  private NotificationEventDTO notificationEventDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    EmailMessageDTO emailMessageDTO = new EmailMessageDTO();
    emailMessageDTO.setDestination("test@example.com");
    emailMessageDTO.setSubject("Test Subject");
    notificationEventDTO = new NotificationEventDTO();
    notificationEventDTO.setDetails(emailMessageDTO);
    notificationEventDTO.setMessage("Test message");
  }

  @Test
  void testSendSuccessfully() {
    when(cryptoUtil.decrypt(anyString())).thenReturn("decrypted");

    SendEmailResponse sendEmailResponse = SendEmailResponse.builder().messageId("123").build();
    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(sendEmailResponse);

    boolean result = awsEmailNotification.send(notificationEventDTO);

    assertTrue(result);
    verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
  }

  @Test
  void testSendFailedWithSesException() {
    when(cryptoUtil.decrypt(anyString())).thenReturn("decrypted");

    SesException sesException =
        (SesException)
            SesException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorMessage("Test SES Error").build())
                .build();
    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(sesException);

    NotificationConsumerException exception =
        assertThrows(
            NotificationConsumerException.class,
            () -> awsEmailNotification.send(notificationEventDTO));

    assertEquals("Error sending sms via SNS. clientId: null", exception.getMessage());
  }

  @Test
  void testSendWithNullResponse() {
    when(cryptoUtil.decrypt(anyString())).thenReturn("decrypted");

    SendEmailResponse sendEmailResponse = SendEmailResponse.builder().messageId("").build();
    when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(sendEmailResponse);

    boolean result = awsEmailNotification.send(notificationEventDTO);

    assertFalse(result);
    verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
  }
}
