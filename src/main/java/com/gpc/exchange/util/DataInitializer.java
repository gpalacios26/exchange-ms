package com.gpc.exchange.util;

import com.gpc.exchange.model.Exchange;
import com.gpc.exchange.repository.ExchangeRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DataInitializer {

  private final ExchangeRepository repository;

  private final DatabaseClient databaseClient;

  @PostConstruct
  public void initData() {
    createTableIfNotExists()
        .thenMany(repository.deleteAll())
        .thenMany(Flux.just(
                new Exchange(null, 3.50, 3.70, LocalDateTime.of(2025, 5, 9, 10, 20)),
                new Exchange(null, 3.55, 3.70, LocalDateTime.of(2025, 5, 9, 10, 30)),
                new Exchange(null, 3.60, 3.80, LocalDateTime.of(2025, 5, 9, 10, 40)),
                new Exchange(null, 3.65, 3.80, LocalDateTime.of(2025, 5, 9, 11, 45)),
                new Exchange(null, 3.70, 3.90, LocalDateTime.of(2025, 5, 9, 11, 55))
            ).flatMap(repository::save)
        ).subscribe(exchange -> System.out.println("Inserted: " + exchange));
  }

  private Mono<Void> createTableIfNotExists() {
    String createTableSql = """
        CREATE TABLE IF NOT EXISTS exchange (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            buy DOUBLE NOT NULL,
            sell DOUBLE NOT NULL,
            date_time TIMESTAMP NOT NULL
        )
        """;
    return databaseClient.sql(createTableSql).then();
  }
}
