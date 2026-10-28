package com.vichumeats.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Small single-instance fixed-window throttle. Do not trust unverified forwarding headers. */
@org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE + 10)
@Component
public class AbuseFilter extends OncePerRequestFilter {
  private record Counter(long start, int count) {}

  private final Map<String, Counter> hits = new HashMap<>();

  protected void doFilterInternal(HttpServletRequest r, HttpServletResponse s, FilterChain c)
      throws ServletException, IOException {
    String path = r.getRequestURI();
    boolean login = path.equals("/login");
    if (r.getMethod().equals("POST")
        && (login || path.equals("/checkout") || path.equals("/sheep"))) {
      long now = System.currentTimeMillis();
      String key = r.getRemoteAddr() + ":" + path;
      boolean blocked;
      synchronized (hits) {
        hits.entrySet().removeIf(e -> now - e.getValue().start() > 600000);
        Counter old = hits.get(key);
        int n = old == null ? 1 : old.count() + 1;
        hits.put(key, new Counter(old == null ? now : old.start(), n));
        blocked = n > (login ? 15 : 20);
        if (hits.size() > 10000) blocked = true;
      }
      if (blocked) {
        s.setStatus(429);
        s.setHeader("Retry-After", "600");
        s.getWriter().write("Too many requests. Please try again in ten minutes.");
        return;
      }
    }
    c.doFilter(r, s);
  }
}
