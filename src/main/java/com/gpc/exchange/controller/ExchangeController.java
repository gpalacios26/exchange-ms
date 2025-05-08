package com.gpc.exchange.controller;

import com.gpc.exchange.dto.ExchangeDTO;
import com.gpc.exchange.dto.ProfileExchangeDTO;
import com.gpc.exchange.dto.ProfileFilterDTO;
import com.gpc.exchange.model.Exchange;
import com.gpc.exchange.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
@Tag(name = "Exchange API", description = "Documentación de la API de Divisas")
public class ExchangeController {

  private final ExchangeService service;

  private final ModelMapper modelMapper;

  @GetMapping
  @Operation(summary = "Listar Posturas Divisas", description = "Devuelve todos los registros de posturas de compra y venta de divisas")
  public Mono<ResponseEntity<Flux<ExchangeDTO>>> findAll() {
    Flux<ExchangeDTO> fx = service.findAll().map(this::convertToDto);
    return Mono.just(ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(fx)
    ).defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping
  @Operation(summary = "Crear Postura Divisas", description = "Registro de una postura de compra y venta de divisas")
  public Mono<ResponseEntity<ExchangeDTO>> create(@Valid @RequestBody ExchangeDTO dto, final ServerHttpRequest req) {
    return service.save(this.convertToModel(dto))
        .map(model -> ResponseEntity
            .created(URI.create(req.getURI().toString().concat("/").concat(String.valueOf(model.getId()))))
            .contentType(MediaType.APPLICATION_JSON)
            .body(this.convertToDto(model))
        ).defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @GetMapping(value = "/realtime", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @Operation(summary = "Postura Divisas - Tiempo Real", description = "Devuelve las posturas de compra y venta de divisas en tiempo real")
  public Flux<ExchangeDTO> streamExchange() {
    return service.streamExchange();
  }

  @GetMapping("/profile/search")
  @Operation(summary = "Postura Divisas - Perfil", description = "Devuelve una postura de compra y venta de divisas por perfil")
  public Mono<ResponseEntity<ProfileExchangeDTO>> findProfileExchange(@Valid @ModelAttribute ProfileFilterDTO dto) {
    return service.findProfileExchange(dto)
        .map(exchange -> ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(exchange)
        )
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  private ExchangeDTO convertToDto(Exchange model) {
    return modelMapper.map(model, ExchangeDTO.class);
  }

  private Exchange convertToModel(ExchangeDTO dto) {
    return modelMapper.map(dto, Exchange.class);
  }
}
