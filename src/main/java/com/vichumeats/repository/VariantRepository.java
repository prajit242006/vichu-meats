package com.vichumeats.repository;

import com.vichumeats.entity.Variant;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantRepository extends JpaRepository<Variant, Long> {
  @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
  @org.springframework.data.jpa.repository.Query("select v from Variant v where v.id = :id")
  Optional<Variant> lockById(@org.springframework.data.repository.query.Param("id") Long id);
}
