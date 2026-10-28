package com.vichumeats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VichuApplication {
  static {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Kolkata"));
  }

  public static void main(String[] args) {
    SpringApplication.run(VichuApplication.class, args);
  }
}
