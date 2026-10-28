package com.vichumeats.repository;

import com.vichumeats.entity.AdminUser;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, String> {}
