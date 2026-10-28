package com.vichumeats.config;

import java.time.*;
import org.springframework.context.annotation.*;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class ValidationConfig {
  @Bean
  public LocalValidatorFactoryBean validator() {
    var factory = new LocalValidatorFactoryBean();
    factory.setConfigurationInitializer(
        c -> c.clockProvider(() -> Clock.system(ZoneId.of("Asia/Kolkata"))));
    return factory;
  }
}
