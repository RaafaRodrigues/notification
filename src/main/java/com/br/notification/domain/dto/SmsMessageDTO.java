package com.br.notification.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsMessageDTO extends NotificationMessageDTO {
  private String number;
}
