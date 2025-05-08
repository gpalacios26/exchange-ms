package com.gpc.exchange.service;

import com.gpc.exchange.dto.ExchangeDTO;
import com.gpc.exchange.dto.ProfileExchangeDTO;
import com.gpc.exchange.dto.ProfileFilterDTO;
import com.gpc.exchange.model.Exchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ExchangeService {

  Flux<Exchange> findAll();

  Mono<Exchange> save(Exchange exchange);

  Flux<ExchangeDTO> streamExchange();

  Mono<ProfileExchangeDTO> findProfileExchange(ProfileFilterDTO dto);
}
