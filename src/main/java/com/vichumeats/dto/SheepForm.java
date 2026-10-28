package com.vichumeats.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class SheepForm {
  @NotBlank
  @Size(max = 100)
  private String customerName;

  @NotBlank
  @Pattern(regexp = "[0-9+() -]{7,20}")
  private String phone;

  @NotNull
  @FutureOrPresent
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate preferredDate;

  @Size(max = 100)
  private String festival;

  @Min(1)
  @Max(100)
  private int sheepCount = 1;

  @NotBlank
  @Size(max = 120)
  private String weightRange;

  @Size(max = 1500)
  private String notes;

  @NotBlank private String token;
}
