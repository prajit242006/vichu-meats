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
public class CustomerOrder {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version private Long version;

  @Column(unique = true, nullable = false)
  private String reference;

  @Column(unique = true, nullable = false)
  private String submissionKey;

  private String customerName;
  private String phone;
  private String fulfilment;
  private String address;
  private String deliveryArea;
  private LocalDate requestedDate;

  @Column(length = 2000)
  private String notes;

  private String payment;
  private String status = "Requested";
  private boolean demo;
  private OffsetDateTime createdAt = OffsetDateTime.now(ZoneId.of("Asia/Kolkata"));

  @Column(precision = 14, scale = 2)
  private BigDecimal subtotal;

  @Column(precision = 14, scale = 2)
  private BigDecimal deliveryFee;

  @Column(precision = 14, scale = 2)
  private BigDecimal total;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "order_id")
  private List<OrderItem> items = new ArrayList<>();
}
