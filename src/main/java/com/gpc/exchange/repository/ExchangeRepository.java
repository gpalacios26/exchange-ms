package com.gpc.exchange.repository;

import com.gpc.exchange.model.Exchange;
import java.time.LocalDateTime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExchangeRepository extends ReactiveCrudRepository<Exchange, Long> {

  Flux<Exchange> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
