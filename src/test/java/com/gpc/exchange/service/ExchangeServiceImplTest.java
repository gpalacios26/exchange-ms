package com.gpc.exchange.service;

import static org.junit.jupiter.api.Assertions.*;

import com.gpc.exchange.dto.ExchangeDTO;
import com.gpc.exchange.dto.ProfileFilterDTO;
import com.gpc.exchange.model.Exchange;
import com.gpc.exchange.repository.ExchangeRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ExchangeServiceImplTest {

  @InjectMocks
  private ExchangeServiceImpl service;

  @Mock
  private ExchangeRepository repository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFindAll() {
    // Arrange
    Exchange exchange1 = new Exchange(1L, 100.0, 200.0, LocalDateTime.now());
    Exchange exchange2 = new Exchange(2L, 150.0, 250.0, LocalDateTime.now());
    Mockito.when(repository.findAll()).thenReturn(Flux.fromIterable(Arrays.asList(exchange1, exchange2)));

    // Act & Assert
    StepVerifier.create(service.findAll())
        .expectNext(exchange1)
        .expectNext(exchange2)
        .verifyComplete();

    Mockito.verify(repository, Mockito.times(1)).findAll();
  }

  @Test
  void testSave() {
    // Arrange
    Exchange exchange = new Exchange(1L, 100.0, 200.0, LocalDateTime.now());
    Mockito.when(repository.save(exchange)).thenReturn(Mono.just(exchange));

    // Act & Assert
    StepVerifier.create(service.save(exchange))
        .expectNext(exchange)
        .verifyComplete();

    Mockito.verify(repository, Mockito.times(1)).save(exchange);
  }

  @Test
  void testStreamExchange() {
    // Arrange
    LocalDateTime now = LocalDateTime.now();
    ExchangeDTO exchangeDTO1 = new ExchangeDTO(1L, 100.0, 200.0, now);
    ExchangeDTO exchangeDTO2 = new ExchangeDTO(2L, 150.0, 250.0, now);

    Mockito.when(repository.save(Mockito.any(Exchange.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    service.save(new Exchange(1L, 100.0, 200.0, now)).block(); // Emit exchangeDTO1
    service.save(new Exchange(2L, 150.0, 250.0, now)).block(); // Emit exchangeDTO2

    // Act & Assert
    StepVerifier.create(service.streamExchange())
        .expectNext(exchangeDTO1)
        .expectNext(exchangeDTO2)
        .thenCancel()
        .verify();
  }

  @ParameterizedTest
  @CsvSource({
      "LOW, 200.0, 300.0",
      "MEDIUM, 150.0, 250.0",
      "HIGH, 100.0, 200.0"
  })
  void testFindProfileExchange(String profile, double expectedBuy, double expectedSell) {
    // Arrange
    LocalDate date = LocalDate.now();
    LocalDateTime now = LocalDateTime.now();
    ProfileFilterDTO filterDTO = new ProfileFilterDTO(profile, date);

    List<Exchange> exchanges = List.of(
        new Exchange(1L, 100.0, 200.0, now),
        new Exchange(2L, 150.0, 250.0, now),
        new Exchange(3L, 200.0, 300.0, now)
    );

    Mockito.when(repository.findByDateTimeBetween(Mockito.any(), Mockito.any()))
        .thenReturn(Flux.fromIterable(exchanges));

    // Act & Assert
    StepVerifier.create(service.findProfileExchange(filterDTO))
        .assertNext(profileExchangeDTO -> {
          assertEquals(expectedBuy, profileExchangeDTO.getBuy());
          assertEquals(expectedSell, profileExchangeDTO.getSell());
        })
        .verifyComplete();

    Mockito.verify(repository, Mockito.times(1)).findByDateTimeBetween(Mockito.any(), Mockito.any());
  }

  @Test
  void testFindProfileExchangeInvalidProfile() {
    // Arrange
    LocalDate date = LocalDate.now();
    LocalDateTime now = LocalDateTime.now();
    ProfileFilterDTO filterDTO = new ProfileFilterDTO("INVALID", date);

    List<Exchange> exchanges = List.of(new Exchange(1L, 100.0, 200.0, now));

    Mockito.when(repository.findByDateTimeBetween(Mockito.any(), Mockito.any()))
        .thenReturn(Flux.fromIterable(exchanges));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      service.findProfileExchange(filterDTO).block();
    });
  }
}