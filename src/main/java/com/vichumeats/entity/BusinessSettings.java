package com.vichumeats.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BusinessSettings {
  @Id private Long id = 1L;
  private String phone = "";
  private String whatsapp = "";
  private String hours = "";
  private String instagram = "";
  private String facebook = "";

  @Column(length = 4000)
  private String story =
      "Vichu Meats is a local farming and livestock startup in Thummankurichi, Namakkal. Explore"
          + " chicken, farm milk in glass containers, naatu kozhi eggs and whole-sheep enquiries.";

  private String farmImage = "farm.svg";
  private String sheepBasis = "To be confirmed by owner";
  private boolean deliveryEnabled = false;
  private boolean cashEnabled = false;
  private boolean upiEnabled = false;
}
