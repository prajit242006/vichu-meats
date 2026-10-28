package com.vichumeats.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductForm {
  private Long id;

  @NotBlank
  @Size(max = 100)
  private String name;

  @Pattern(regexp = "Chicken|Milk|Eggs")
  private String category = "Chicken";

  @NotBlank
  @Size(max = 1500)
  private String description;

  private boolean active = true;
  private boolean demo = true;
}
