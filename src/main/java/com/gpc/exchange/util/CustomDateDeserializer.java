package com.gpc.exchange.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gpc.exchange.exception.InvalidDateFormatException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomDateDeserializer extends JsonDeserializer<LocalDate> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public LocalDate deserialize(JsonParser p, DeserializationContext ctx) throws InvalidDateFormatException {
    try {
      String date = p.getText();
      return LocalDate.parse(date, FORMATTER);
    } catch (Exception e) {
      throw new InvalidDateFormatException("El formato de la fecha es inválido. Debe ser yyyy-MM-dd.");
    }
  }
}
