package com.vichumeats.config;

import com.vichumeats.repository.AdminUserRepository;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService users(AdminUserRepository repo) {
    return name ->
        repo.findById(name)
            .map(
                u ->
                    User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .roles("ADMIN")
                        .build())
            .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
  }

  @Bean
  SecurityFilterChain chain(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            a -> a.requestMatchers("/admin", "/admin/**").hasRole("ADMIN").anyRequest().permitAll())
        .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/admin", true).permitAll())
        .logout(l -> l.logoutSuccessUrl("/login?logout"))
        .headers(
            h ->
                h.contentSecurityPolicy(
                    c ->
                        c.policyDirectives(
                            "default-src 'self'; img-src 'self' data:; style-src 'self'; script-src"
                                + " 'self'; base-uri 'self'; form-action 'self'; frame-ancestors"
                                + " 'none'")))
        .build();
  }
}
