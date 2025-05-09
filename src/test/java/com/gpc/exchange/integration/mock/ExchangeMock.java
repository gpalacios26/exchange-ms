package com.gpc.exchange.integration.mock;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeMock {

  private Long id;

  private Double buy;

  private Double sell;

  private LocalDateTime dateTime;
}
