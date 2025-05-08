package com.gpc.exchange.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Value("${swagger.apiInfo.title}")
  private String title;

  @Value("${swagger.apiInfo.description}")
  private String description;

  @Value("${swagger.apiInfo.contact.name}")
  private String nameContact;

  @Value("${swagger.apiInfo.contact.url}")
  private String urlContact;

  @Value("${swagger.apiInfo.contact.email}")
  private String emailContact;

  @Value("${swagger.apiInfo.license}")
  private String license;

  @Value("${swagger.apiInfo.licenseUrl}")
  private String licenseUrl;

  @Value("${swagger.apiInfo.version}")
  private String version;

  @Bean
  public OpenAPI api() {
    return new OpenAPI().info(apiEndPointsInfo());
  }

  private Info apiEndPointsInfo() {
    return new Info().title(title).description(description)
        .contact(new Contact().name(nameContact).url(urlContact).email(emailContact))
        .license(new License().name(license).url(licenseUrl)).version(version);
  }
}
