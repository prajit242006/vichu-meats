package com.vichumeats.repository;

import com.vichumeats.entity.Product;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
