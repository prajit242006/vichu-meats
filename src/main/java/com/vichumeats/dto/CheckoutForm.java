package com.vichumeats.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class CheckoutForm {
  @NotBlank
  @Size(max = 100)
  private String customerName;

  @NotBlank
  @Pattern(regexp = "[0-9+() -]{7,20}")
  private String phone;

  @NotBlank
  @Pattern(regexp = "Pickup|Delivery")
  private String fulfilment = "Pickup";

  @Size(max = 500)
  private String address;

  private Long areaId;

  @NotNull
  @FutureOrPresent
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate requestedDate;

  @Size(max = 1500)
  private String notes;

  @Size(max = 40)
  private String payment = "Not selected";

  @NotBlank private String token;
}
