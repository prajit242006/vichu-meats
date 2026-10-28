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
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String productName;
  private String variantLabel;
  private String unit;
  private int quantity;

  @Column(precision = 12, scale = 2)
  private BigDecimal unitPrice;

  @Column(precision = 12, scale = 2)
  private BigDecimal deposit;

  @Column(precision = 14, scale = 2)
  private BigDecimal lineTotal;
}
