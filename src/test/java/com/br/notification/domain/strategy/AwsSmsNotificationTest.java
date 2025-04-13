package com.br.notification.domain.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.br.notification.domain.dto.NotificationEventDTO;
import com.br.notification.domain.dto.SmsMessageDTO;
import com.br.notification.domain.exception.NotificationConsumerException;
import com.br.notification.domain.security.CryptoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

class AwsSmsNotificationTest {

  @Mock private SnsClient snsClient;

  @Mock private CryptoUtil cryptoUtil;

  @InjectMocks private AwsSmsNotification awsSmsNotification;

  private NotificationEventDTO notificationEventDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    SmsMessageDTO smsMessageDTO = new SmsMessageDTO();
    smsMessageDTO.setNumber("1234567890");
    notificationEventDTO = new NotificationEventDTO();
    notificationEventDTO.setDetails(smsMessageDTO);
    notificationEventDTO.setMessage("Test SMS message");
  }

  @Test
  void testSendSuccessfully() {
    when(cryptoUtil.decrypt(anyString())).thenReturn("decrypted");

    PublishResponse publishResponse = PublishResponse.builder().messageId("123").build();
    when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

    boolean result = awsSmsNotification.send(notificationEventDTO);

    assertTrue(result);
    verify(snsClient, times(1)).publish(any(PublishRequest.class));
  }

  @Test
  void testSendFailedWithSnsException() {
    when(cryptoUtil.decrypt(anyString())).thenReturn("decrypted");

    SnsException snsException =
        (SnsException)
            SnsException.builder()
                .awsErrorDetails(AwsErrorDetails.builder().errorMessage("Test SNS Error").build())
                .build();
    when(snsClient.publish(any(PublishRequest.class))).thenThrow(snsException);

    NotificationConsumerException exception =
        assertThrows(
            NotificationConsumerException.class,
            () -> {
              awsSmsNotification.send(notificationEventDTO);
            });

    assertEquals("Error sending sms via SNS. clientId: null", exception.getMessage());
  }

  @Test
  void testSendWithNullResponse() {
    when(cryptoUtil.decrypt(anyString())).thenReturn("decrypted");

    PublishResponse publishResponse = PublishResponse.builder().messageId("").build();
    when(snsClient.publish(any(PublishRequest.class))).thenReturn(publishResponse);

    boolean result = awsSmsNotification.send(notificationEventDTO);

    assertFalse(result);
    verify(snsClient, times(1)).publish(any(PublishRequest.class));
  }
}
