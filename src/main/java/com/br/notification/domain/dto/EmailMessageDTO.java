package com.br.notification.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessageDTO extends NotificationMessageDTO {
  private String destination;
  private String subject;
}
