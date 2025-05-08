package com.gpc.exchange.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gpc.exchange.util.CustomDateDeserializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileFilterDTO {

  @NotNull(message = "El campo profile no puede ser nulo")
  @Pattern(regexp = "LOW|MEDIUM|HIGH", message = "El campo profile debe ser 'LOW' o 'MEDIUM' o 'HIGH'")
  private String profile;

  @NotNull(message = "El campo date no puede ser nulo")
  @JsonDeserialize(using = CustomDateDeserializer.class)
  private LocalDate date;
}
