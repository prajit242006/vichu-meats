package com.vichumeats.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SheepEnquiry {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version private Long version;

  @Column(unique = true)
  private String reference;

  @Column(unique = true)
  private String submissionKey;

  private String customerName;
  private String phone;
  private LocalDate preferredDate;
  private String festival;
  private int sheepCount;
  private String weightRange;

  @Column(length = 2000)
  private String notes;

  private String status = "Requested";

  @Column(precision = 14, scale = 2)
  private BigDecimal estimatedPrice;

  private String pricingBasis;

  @Column(length = 2000)
  private String ownerNotes;

  private OffsetDateTime createdAt = OffsetDateTime.now(ZoneId.of("Asia/Kolkata"));
}
