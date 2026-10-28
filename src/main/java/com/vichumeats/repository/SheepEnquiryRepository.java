package com.vichumeats.repository;

import com.vichumeats.entity.SheepEnquiry;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SheepEnquiryRepository extends JpaRepository<SheepEnquiry, Long> {
  Optional<SheepEnquiry> findBySubmissionKey(String key);
}
