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
public class Variant {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version private Long version;

  @ManyToOne(optional = false)
  private Product product;

  private String label;
  private String unit;

  @Column(precision = 12, scale = 2)
  private BigDecimal price;

  @Column(precision = 12, scale = 2)
  private BigDecimal deposit = BigDecimal.ZERO;

  private boolean depositEnabled = false;
  private boolean available = true;
}
