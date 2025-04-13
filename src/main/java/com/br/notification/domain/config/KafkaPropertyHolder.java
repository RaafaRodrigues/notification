package com.br.notification.domain.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public final class KafkaPropertyHolder {
  @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
  private String bootstrapServer;

  @Value("${kafka.acordo.efetivado.topic:acordo.efetivado.0}")
  private String agreementCarriedOut;

  @Value("${kafka.acordo.quebrado.topic:acordo.quebrado.0}")
  private String agreementBroken;

  @Value("${kafka.pag.efetuado.topic:pag.efetuado.0}")
  private String pagCarrieOut;

  @Value("${kafka.pag.nao.efetuado.topic:pag.nao.efetuado.0}")
  private String pagNotCarrieOut;
}
