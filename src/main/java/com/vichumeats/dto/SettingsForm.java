package com.vichumeats.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SettingsForm {
  @Pattern(regexp = "[0-9+() -]{0,20}")
  private String phone = "";

  @Pattern(regexp = "(?:[1-9][0-9]{7,14})?")
  private String whatsapp = "";

  @Size(max = 200)
  private String hours = "";

  @Pattern(regexp = "(?:https://(?:www[.])?instagram[.]com/[A-Za-z0-9_./-]+)?")
  private String instagram = "";

  @Pattern(regexp = "(?:https://(?:www[.])?facebook[.]com/[A-Za-z0-9_./-]+)?")
  private String facebook = "";

  @NotBlank
  @Size(max = 3500)
  private String story;

  @NotBlank
  @Size(max = 120)
  private String sheepBasis;

  private boolean deliveryEnabled;
  private boolean cashEnabled;
  private boolean upiEnabled;
}
