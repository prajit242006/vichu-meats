package com.vichumeats.repository;

import com.vichumeats.entity.CustomerOrder;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
  Optional<CustomerOrder> findBySubmissionKey(String key);

  long countByStatus(String status);
}
