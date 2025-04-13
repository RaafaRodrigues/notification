package com.br.notification.domain.dto;

import com.br.notification.domain.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = EmailMessageDTO.class, name = "EMAIL"),
  @JsonSubTypes.Type(value = SmsMessageDTO.class, name = "SMS")
})
public abstract class NotificationMessageDTO {
  private NotificationType type;
}
