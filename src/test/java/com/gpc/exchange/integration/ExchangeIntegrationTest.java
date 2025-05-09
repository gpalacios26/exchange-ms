package com.gpc.exchange.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.gpc.exchange.controller.ExchangeController;
import com.gpc.exchange.dto.ExchangeDTO;
import com.gpc.exchange.dto.ProfileExchangeDTO;
import com.gpc.exchange.integration.mock.ExchangeMock;
import com.gpc.exchange.model.Exchange;
import com.gpc.exchange.service.ExchangeService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ExchangeController.class)
class ExchangeIntegrationTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private ExchangeService service;

  @MockitoBean
  private ModelMapper modelMapper;

  @MockitoBean
  private WebProperties.Resources resources;

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

    // Act & Assert
    webTestClient.get()
        .uri("/api/exchange")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBodyList(ExchangeDTO.class)
        .value(response -> {
          StepVerifier.create(Flux.fromIterable(response))
              .expectNext(exchangeDTO1, exchangeDTO2)
              .verifyComplete();
        });
  }

  @Test
  void testCreate() {
    // Arrange
    LocalDateTime dateTime = LocalDateTime.parse("2025-05-09T18:14:01.184");
    ExchangeMock mock = new ExchangeMock();
    mock.setBuy(100.0);
    mock.setSell(200.0);
    mock.setDateTime(dateTime);

    Exchange model = new Exchange();
    model.setId(1L);
    model.setBuy(100.0);
    model.setSell(200.0);
    mock.setDateTime(dateTime);

    ExchangeDTO outputDto = new ExchangeDTO();
    outputDto.setId(1L);
    outputDto.setBuy(100.0);
    outputDto.setSell(200.0);

    Mockito.when(service.save(Mockito.any())).thenReturn(Mono.just(model));
    Mockito.when(modelMapper.map(model, ExchangeDTO.class)).thenReturn(outputDto);

    // Act & Assert
    webTestClient.post()
        .uri("/api/exchange")
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(mock))
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectHeader().valueMatches("Location", ".*/api/exchange/1")
        .expectBody(ExchangeDTO.class)
        .value(response -> {
          assertEquals(outputDto.getId(), response.getId());
          assertEquals(outputDto.getBuy(), response.getBuy());
          assertEquals(outputDto.getSell(), response.getSell());
        });
  }

  @Test
  void testStreamExchange() {
    // Arrange
    ExchangeDTO exchangeDTO1 = new ExchangeDTO(1L, 100.0, 200.0, LocalDateTime.parse("2025-05-09T14:29:07"));
    ExchangeDTO exchangeDTO2 = new ExchangeDTO(2L, 150.0, 250.0, LocalDateTime.parse("2025-05-09T14:30:07"));

    Mockito.when(service.streamExchange()).thenReturn(Flux.just(exchangeDTO1, exchangeDTO2));

    // Act & Assert
    webTestClient.get()
        .uri("/api/exchange/realtime")
        .accept(MediaType.TEXT_EVENT_STREAM)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
        .returnResult(ExchangeDTO.class)
        .getResponseBody()
        .as(StepVerifier::create)
        .expectNext(exchangeDTO1, exchangeDTO2)
        .verifyComplete();
  }

  @Test
  void testFindProfileExchange() {
    // Arrange
    ProfileExchangeDTO profileExchangeDTO = new ProfileExchangeDTO();

    Mockito.when(service.findProfileExchange(Mockito.any())).thenReturn(Mono.just(profileExchangeDTO));

    // Act & Assert
    webTestClient.get()
        .uri("/api/exchange/profile/search?profile=LOW&date=2025-05-09")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON)
        .expectBody(ProfileExchangeDTO.class)
        .value(response -> {
          assertEquals(profileExchangeDTO, response);
        });
  }
}