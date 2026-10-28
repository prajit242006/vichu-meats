package com.vichumeats.config;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Value("${app.uploads}")
  String uploads;

  public void addResourceHandlers(ResourceHandlerRegistry r) {
    r.addResourceHandler("/uploads/**")
        .addResourceLocations(Path.of(uploads).toAbsolutePath().toUri().toString() + "/");
  }
}
