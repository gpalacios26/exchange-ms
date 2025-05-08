package com.gpc.exchange.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gpc.exchange.util.CustomDateTimeDeserializer;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeDTO {

  private Long id;

  @NotNull(message = "El campo buy no puede ser nulo")
  private Double buy;

  @NotNull(message = "El campo sell no puede ser nulo")
  private Double sell;

  @NotNull(message = "El campo date time no puede ser nulo")
  @JsonDeserialize(using = CustomDateTimeDeserializer.class)
  private LocalDateTime dateTime;
}
