package com.gpc.exchange.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

  @Bean("defaultMapper")
  public ModelMapper defaultMapper(){
    return new ModelMapper();
  }
}
