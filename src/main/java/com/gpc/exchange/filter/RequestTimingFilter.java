package com.gpc.exchange.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RequestTimingFilter implements WebFilter {

  private static final String HEADER_NAME = "X-Request-Duration";

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    long startTime = System.currentTimeMillis();

    exchange.getResponse().beforeCommit(() -> {
      long duration = System.currentTimeMillis() - startTime;
      exchange.getResponse().getHeaders().add(HEADER_NAME, duration + "ms");
      return Mono.empty();
    });

    return chain.filter(exchange);
  }
}
