package com.gpc.exchange.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gpc.exchange.exception.InvalidDateFormatException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class CustomDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

  private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
      .optionalStart()
      .appendPattern("[.SSS][.SS][.S]")
      .optionalEnd()
      .toFormatter();

  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws InvalidDateFormatException {
    try {
      String date = p.getText();
      return LocalDateTime.parse(date, FORMATTER);
    } catch (Exception e) {
      throw new InvalidDateFormatException("El formato de la fecha y hora es inválido. Debe ser yyyy-MM-dd'T'HH:mm:ss");
    }
  }
}
