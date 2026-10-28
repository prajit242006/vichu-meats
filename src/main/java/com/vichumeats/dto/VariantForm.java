package com.vichumeats.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class VariantForm {
  private Long id;
  @NotNull private Long productId;

  @NotBlank
  @Size(max = 80)
  private String label;

  @NotBlank
  @Size(max = 30)
  private String unit;

  @DecimalMin("0.01")
  @DecimalMax("9999999")
  @Digits(integer = 7, fraction = 2)
  private BigDecimal price;

  @NotNull
  @DecimalMin("0")
  @DecimalMax("99999")
  @Digits(integer = 5, fraction = 2)
  private BigDecimal deposit = BigDecimal.ZERO;

  private boolean depositEnabled;
  private boolean available = true;
}
