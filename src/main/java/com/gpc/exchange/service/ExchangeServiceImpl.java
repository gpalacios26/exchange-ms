package com.gpc.exchange.service;

import com.gpc.exchange.dto.ExchangeDTO;
import com.gpc.exchange.dto.ProfileExchangeDTO;
import com.gpc.exchange.dto.ProfileFilterDTO;
import com.gpc.exchange.model.Exchange;
import com.gpc.exchange.repository.ExchangeRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

  private final ExchangeRepository repository;

  private final Sinks.Many<ExchangeDTO> sink = Sinks.many().multicast().onBackpressureBuffer();

  @Override
  public Flux<Exchange> findAll() {
    return repository.findAll();
  }

  @Override
  public Mono<Exchange> save(Exchange exchange) {
    return repository.save(exchange).doOnNext(saved -> {
      ExchangeDTO exchangeDTO = new ExchangeDTO(saved.getId(), saved.getBuy(), saved.getSell(), saved.getDateTime());
      sink.tryEmitNext(exchangeDTO);
    });
  }

  @Override
  public Flux<ExchangeDTO> streamExchange() {
    return sink.asFlux();
  }

  @Override
  public Mono<ProfileExchangeDTO> findProfileExchange(ProfileFilterDTO dto) {
    return findByDate(dto.getDate())
        .collectList()
        .flatMap(exchanges -> getProfileExchange(dto.getProfile(), exchanges));
  }

  private Flux<Exchange> findByDate(LocalDate date) {
    LocalDateTime startOfDay = date.atStartOfDay();
    LocalDateTime endOfDay = date.atTime(23, 59, 59);
    return repository.findByDateTimeBetween(startOfDay, endOfDay);
  }

  private Mono<ProfileExchangeDTO> getProfileExchange(String profile, List<Exchange> exchanges) {
    return switch (profile) {
      case "LOW" -> getLowProfileExchange(exchanges);
      case "MEDIUM" -> getMediumProfileExchange(exchanges);
      case "HIGH" -> getHighProfileExchange(exchanges);
      default -> Mono.error(new IllegalArgumentException("Invalid profile type"));
    };
  }

  private Mono<ProfileExchangeDTO> getLowProfileExchange(List<Exchange> exchanges) {
    double buy = exchanges.stream().mapToDouble(Exchange::getBuy).max().orElse(0);
    double sell = exchanges.stream().mapToDouble(Exchange::getSell).max().orElse(0);
    return getProfileExchangeDTO(buy, sell);
  }

  private Mono<ProfileExchangeDTO> getMediumProfileExchange(List<Exchange> exchanges) {
    double buy = exchanges.stream().mapToDouble(Exchange::getBuy).average().orElse(0);
    double sell = exchanges.stream().mapToDouble(Exchange::getSell).average().orElse(0);
    return getProfileExchangeDTO(buy, sell);
  }

  private Mono<ProfileExchangeDTO> getHighProfileExchange(List<Exchange> exchanges) {
    double buy = exchanges.stream().mapToDouble(Exchange::getBuy).min().orElse(0);
    double sell = exchanges.stream().mapToDouble(Exchange::getSell).min().orElse(0);
    return getProfileExchangeDTO(buy, sell);
  }

  private Mono<ProfileExchangeDTO> getProfileExchangeDTO(double buy, double sell) {
    ProfileExchangeDTO profileExchangeDTO = new ProfileExchangeDTO(getValueRound(buy), getValueRound(sell));
    return Mono.just(profileExchangeDTO);
  }

  private Double getValueRound(Double value) {
    BigDecimal result = new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    return result.doubleValue();
  }
}
