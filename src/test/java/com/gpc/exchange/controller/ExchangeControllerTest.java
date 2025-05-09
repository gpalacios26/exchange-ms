package com.gpc.exchange.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.gpc.exchange.dto.ExchangeDTO;
import com.gpc.exchange.dto.ProfileExchangeDTO;
import com.gpc.exchange.dto.ProfileFilterDTO;
import com.gpc.exchange.model.Exchange;
import com.gpc.exchange.service.ExchangeService;
import java.net.URI;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ExchangeControllerTest {

  @InjectMocks
  private ExchangeController controller;

  @Mock
  private ExchangeService service;

  @Mock
  private ModelMapper modelMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFindAll() {
    // Arrange
    Exchange exchange1 = new Exchange();
    Exchange exchange2 = new Exchange();
    ExchangeDTO exchangeDTO1 = new ExchangeDTO();
    ExchangeDTO exchangeDTO2 = new ExchangeDTO();

    Mockito.when(service.findAll()).thenReturn(Flux.just(exchange1, exchange2));
    Mockito.when(modelMapper.map(exchange1, ExchangeDTO.class)).thenReturn(exchangeDTO1);
    Mockito.when(modelMapper.map(exchange2, ExchangeDTO.class)).thenReturn(exchangeDTO2);

    // Act
    Mono<ResponseEntity<Flux<ExchangeDTO>>> response = controller.findAll();

    // Assert
    StepVerifier.create(response)
        .assertNext(entity -> {
          StepVerifier.create(Objects.requireNonNull(entity.getBody()))
              .expectNext(exchangeDTO1, exchangeDTO2)
              .verifyComplete();
        })
        .verifyComplete();
  }

  @Test
  void testCreate() {
    // Arrange
    ExchangeDTO inputDto = new ExchangeDTO();
    Exchange model = new Exchange();
    ExchangeDTO outputDto = new ExchangeDTO();
    ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);

    Mockito.when(modelMapper.map(inputDto, Exchange.class)).thenReturn(model);
    Mockito.when(service.save(model)).thenReturn(Mono.just(model));
    Mockito.when(modelMapper.map(model, ExchangeDTO.class)).thenReturn(outputDto);
    Mockito.when(request.getURI()).thenReturn(URI.create("http://localhost/api/exchange"));

    // Act
    Mono<ResponseEntity<ExchangeDTO>> response = controller.create(inputDto, request);

    // Assert
    StepVerifier.create(response)
        .assertNext(entity -> {
          assertEquals(outputDto, entity.getBody());
          assertTrue(Objects.requireNonNull(entity.getHeaders().getLocation()).toString().contains("/api/exchange/"));
        })
        .verifyComplete();
  }

  @Test
  void testStreamExchange() {
    // Arrange
    ExchangeDTO exchangeDTO1 = new ExchangeDTO();
    ExchangeDTO exchangeDTO2 = new ExchangeDTO();

    Mockito.when(service.streamExchange()).thenReturn(Flux.just(exchangeDTO1, exchangeDTO2));

    // Act
    Flux<ExchangeDTO> response = controller.streamExchange();

    // Assert
    StepVerifier.create(response)
        .expectNext(exchangeDTO1, exchangeDTO2)
        .verifyComplete();
  }

  @Test
  void testFindProfileExchange() {
    // Arrange
    ProfileFilterDTO filterDTO = new ProfileFilterDTO();
    ProfileExchangeDTO profileExchangeDTO = new ProfileExchangeDTO();

    Mockito.when(service.findProfileExchange(filterDTO)).thenReturn(Mono.just(profileExchangeDTO));

    // Act
    Mono<ResponseEntity<ProfileExchangeDTO>> response = controller.findProfileExchange(filterDTO);

    // Assert
    StepVerifier.create(response)
        .assertNext(entity -> {
          assertEquals(profileExchangeDTO, entity.getBody());
        })
        .verifyComplete();
  }
}