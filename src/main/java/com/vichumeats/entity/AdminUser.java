package com.vichumeats.entity;

import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AdminUser {
  @Id private String username;
  private String password;
}
