package com.vichumeats.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String category;

  @Column(length = 2000)
  private String description;

  private String image = "eggs.svg";
  private boolean active = true;
  private boolean demo = false;

  @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
  @OrderBy("id")
  private List<Variant> variants = new ArrayList<>();
}
